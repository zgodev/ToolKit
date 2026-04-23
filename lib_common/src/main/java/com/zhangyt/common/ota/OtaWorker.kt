package com.zhangyt.common.ota

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.os.Environment
import com.elvishew.xlog.XLog
import androidx.core.app.NotificationCompat
import androidx.work.CoroutineWorker
import androidx.work.Data
import androidx.work.ForegroundInfo
import androidx.work.WorkerParameters
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.net.HttpURLConnection
import java.net.URL
import java.security.MessageDigest

/**
 * OTA 下载 Worker（支持断点续传）
 *
 * 使用 WorkManager 的 CoroutineWorker 在后台执行 APK 下载任务，
 * 支持断点续传、进度回调和 MD5 校验。
 *
 * 断点续传原理：
 * 1. 下载文件使用 .tmp 临时后缀，完成后再重命名为 .apk
 * 2. 每次启动时检查 .tmp 文件是否存在，获取已下载字节数
 * 3. 通过 HTTP Range 请求头从断点处继续下载
 * 4. 服务端返回 206 表示支持续传；返回 200 表示不支持，需从头开始
 * 5. 被取消/杀死时保留 .tmp 文件，下次自动续传
 *
 * 输入参数（通过 InputData 传递）：
 * - [KEY_DOWNLOAD_URL]  下载地址
 * - [KEY_FILE_MD5]      文件 MD5（可选，用于校验）
 * - [KEY_VERSION_NAME]  版本名（用于通知显示）
 * - [KEY_FILE_SIZE]     文件总大小（可选，用于续传校验）
 *
 * 输出结果（通过 OutputData 返回）：
 * - [KEY_FILE_PATH]     下载完成的文件路径
 *
 * 进度数据：
 * - [KEY_PROGRESS]      当前下载进度（0~100）
 */
class OtaWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    companion object {
        const val TAG = "OtaWorker"

        // InputData keys
        const val KEY_DOWNLOAD_URL = "download_url"
        const val KEY_FILE_MD5 = "file_md5"
        const val KEY_VERSION_NAME = "version_name"
        const val KEY_FILE_SIZE = "file_size"

        // OutputData / Progress keys
        const val KEY_FILE_PATH = "file_path"
        const val KEY_PROGRESS = "progress"

        // 通知相关
        private const val NOTIFICATION_CHANNEL_ID = "ota_download_channel"
        private const val NOTIFICATION_ID = 10086

        // 临时文件后缀
        private const val TEMP_SUFFIX = ".tmp"

        // 进度更新最小间隔（毫秒），避免过于频繁更新
        private const val PROGRESS_UPDATE_INTERVAL_MS = 500L
    }

    /** 通知渠道是否已创建（避免重复创建） */
    private var channelCreated = false

    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        val downloadUrl = inputData.getString(KEY_DOWNLOAD_URL)
        val fileMd5 = inputData.getString(KEY_FILE_MD5) ?: ""
        val versionName = inputData.getString(KEY_VERSION_NAME) ?: "新版本"
        val expectedSize = inputData.getLong(KEY_FILE_SIZE, 0L)

        if (downloadUrl.isNullOrBlank()) {
            XLog.tag(TAG).e("下载地址为空")
            return@withContext Result.failure(
                Data.Builder().putString("error", "下载地址为空").build()
            )
        }

        // 设置前台通知（Android 12+ 需要）
        try {
            setForeground(createForegroundInfo(versionName, 0))
        } catch (e: Exception) {
            XLog.tag(TAG).w("设置前台服务失败: ${e.message}")
        }

        try {
            // 创建下载目录
            val downloadDir = File(
                applicationContext.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS),
                "ota"
            )
            if (!downloadDir.exists()) downloadDir.mkdirs()

            val apkFile = File(downloadDir, "update_${versionName}.apk")
            val tempFile = File(downloadDir, "update_${versionName}.apk$TEMP_SUFFIX")

            // 如果最终文件已存在，说明之前已经下载完成，直接返回
            if (apkFile.exists()) {
                XLog.tag(TAG).d("APK 文件已存在，跳过下载: ${apkFile.absolutePath}")
                // 但仍需校验 MD5（防止文件损坏）
                if (fileMd5.isNotBlank()) {
                    val localMd5 = getFileMd5(apkFile)
                    if (!fileMd5.equals(localMd5, ignoreCase = true)) {
                        XLog.tag(TAG).w("已有文件 MD5 不匹配，删除重新下载")
                        apkFile.delete()
                        tempFile.delete() // 同时清理残留的临时文件
                    } else {
                        return@withContext Result.success(
                            Data.Builder().putString(KEY_FILE_PATH, apkFile.absolutePath).build()
                        )
                    }
                } else {
                    return@withContext Result.success(
                        Data.Builder().putString(KEY_FILE_PATH, apkFile.absolutePath).build()
                    )
                }
            }

            // 检查临时文件，判断是否需要续传
            var downloadedBytes = 0L
            if (tempFile.exists()) {
                downloadedBytes = tempFile.length()
                // 如果已知文件总大小，且临时文件大小超出，说明文件已损坏
                if (expectedSize > 0 && downloadedBytes > expectedSize) {
                    XLog.tag(TAG).w("临时文件大小异常 ($downloadedBytes > $expectedSize)，删除重新下载")
                    tempFile.delete()
                    downloadedBytes = 0L
                } else if (downloadedBytes > 0) {
                    XLog.tag(TAG).d("发现临时文件，已下载 ${downloadedBytes} 字节，尝试断点续传")
                }
            }

            // 开始（或续传）下载
            // 【Bug #1 修复】downloadFile 返回 false 表示被 isStopped 中断，
            //  此时不能继续重命名，直接返回 retry 等下次续传
            val downloadComplete = downloadFile(downloadUrl, tempFile, versionName, downloadedBytes)
            if (!downloadComplete) {
                XLog.tag(TAG).d("下载被中断，保留临时文件等待下次续传")
                return@withContext Result.retry()
            }

            // 下载完成：临时文件重命名为最终文件
            if (apkFile.exists()) apkFile.delete()
            if (!tempFile.renameTo(apkFile)) {
                // rename 失败时 fallback：复制后删除临时文件
                tempFile.copyTo(apkFile, overwrite = true)
                tempFile.delete()
            }
            XLog.tag(TAG).d("临时文件已重命名: ${tempFile.name} -> ${apkFile.name}")

            // MD5 校验
            if (fileMd5.isNotBlank()) {
                val localMd5 = getFileMd5(apkFile)
                if (!fileMd5.equals(localMd5, ignoreCase = true)) {
                    apkFile.delete()
                    XLog.tag(TAG).e("MD5 校验失败: 期望=$fileMd5, 实际=$localMd5")
                    return@withContext Result.failure(
                        Data.Builder().putString("error", "MD5校验失败，请重新下载").build()
                    )
                }
                XLog.tag(TAG).d("MD5 校验通过")
            }

            // 下载成功，返回文件路径
            val outputData = Data.Builder()
                .putString(KEY_FILE_PATH, apkFile.absolutePath)
                .build()

            XLog.tag(TAG).d("下载完成: ${apkFile.absolutePath}")
            Result.success(outputData)

        } catch (e: Exception) {
            XLog.tag(TAG).e("下载失败: ${e.message}", e)
            // 返回 retry，WorkManager 会按退避策略重新调度，
            // 下次执行时自动从 .tmp 断点续传
            Result.retry()
        }
    }

    /**
     * 下载文件（支持断点续传）
     *
     * @param url         下载地址
     * @param outputFile  输出的临时文件（.tmp）
     * @param versionName 版本名（用于通知栏显示）
     * @param startBytes  已下载的字节数（> 0 时发起续传请求）
     * @return true = 下载完成；false = 被 isStopped 中断（临时文件已保留）
     */
    private suspend fun downloadFile(
        url: String,
        outputFile: File,
        versionName: String,
        startBytes: Long
    ): Boolean {
        val connection = (URL(url).openConnection() as HttpURLConnection).apply {
            connectTimeout = 15_000
            readTimeout = 30_000
            requestMethod = "GET"
            // 断点续传：设置 Range 请求头
            if (startBytes > 0) {
                setRequestProperty("Range", "bytes=$startBytes-")
                XLog.tag(TAG).d("请求 Range: bytes=$startBytes-")
            }
        }

        try {
            connection.connect()
            val responseCode = connection.responseCode

            /*
             * 响应码判断：
             * - 206 Partial Content → 服务端支持续传，从 startBytes 继续写入
             * - 200 OK             → 服务端不支持 Range，返回完整文件，需从头写入
             * - 其他               → 错误
             */
            val isResuming: Boolean
            val downloadedBytes: Long

            when (responseCode) {
                HttpURLConnection.HTTP_PARTIAL -> {
                    // 206：服务端支持续传
                    isResuming = true
                    downloadedBytes = startBytes
                    XLog.tag(TAG).d("服务端支持续传 (206)，从 $startBytes 字节继续")
                }
                HttpURLConnection.HTTP_OK -> {
                    // 200：服务端不支持 Range 或返回完整内容
                    isResuming = false
                    downloadedBytes = 0L
                    if (startBytes > 0) {
                        XLog.tag(TAG).w("服务端不支持续传 (200)，从头开始下载")
                        // 清空已有的临时文件
                        if (outputFile.exists()) outputFile.delete()
                    }
                }
                else -> {
                    throw RuntimeException("HTTP 响应码: $responseCode")
                }
            }

            // 【Bug #6 修复】contentLength 可能为 -1（服务端未返回 Content-Length）
            val contentLength = connection.contentLength.toLong()
            val totalBytes = when {
                contentLength > 0 && isResuming -> downloadedBytes + contentLength
                contentLength > 0 -> contentLength
                else -> -1L  // 未知总大小，进度显示为不确定状态
            }

            var currentBytes = downloadedBytes
            // 【优化 #7】记录上次进度更新时间，控制更新频率
            var lastProgressUpdateTime = 0L

            // 断点续传的关键：append 模式打开文件
            connection.inputStream.use { input ->
                FileOutputStream(outputFile, isResuming).use { output ->
                    val buffer = ByteArray(8 * 1024)
                    var bytesRead: Int

                    while (input.read(buffer).also { bytesRead = it } != -1) {
                        // 检查是否被取消——注意：不删除临时文件！
                        if (isStopped) {
                            output.flush()
                            XLog.tag(TAG).d("任务被停止，已保留临时文件 (${currentBytes} 字节)，下次可续传")
                            return false  // 【Bug #1 修复】返回 false 表示未完成
                        }

                        output.write(buffer, 0, bytesRead)
                        currentBytes += bytesRead

                        // 【优化 #7】节流：至少间隔 500ms 才更新一次进度
                        val now = System.currentTimeMillis()
                        if (now - lastProgressUpdateTime >= PROGRESS_UPDATE_INTERVAL_MS) {
                            lastProgressUpdateTime = now

                            val progress = if (totalBytes > 0) {
                                (currentBytes * 100 / totalBytes).toInt().coerceIn(0, 100)
                            } else {
                                -1
                            }

                            // 更新 WorkManager 进度
                            setProgress(
                                Data.Builder().putInt(KEY_PROGRESS, progress).build()
                            )

                            // 更新通知栏进度
                            try {
                                setForeground(createForegroundInfo(versionName, progress))
                            } catch (_: Exception) {
                            }
                        }
                    }
                    output.flush()
                }
            }

            // 最终进度设为 100%
            setProgress(Data.Builder().putInt(KEY_PROGRESS, 100).build())

            XLog.tag(TAG).d("文件写入完成，共 $currentBytes 字节")
            return true  // 下载完成
        } finally {
            connection.disconnect()
        }
    }

    /**
     * 计算文件 MD5
     */
    private fun getFileMd5(file: File): String {
        val digest = MessageDigest.getInstance("MD5")
        file.inputStream().use { input ->
            val buffer = ByteArray(8 * 1024)
            var bytesRead: Int
            while (input.read(buffer).also { bytesRead = it } != -1) {
                digest.update(buffer, 0, bytesRead)
            }
        }
        return digest.digest().joinToString("") { "%02x".format(it) }
    }

    /**
     * 创建前台通知信息（下载进度通知）
     */
    private fun createForegroundInfo(versionName: String, progress: Int): ForegroundInfo {
        val context = applicationContext

        // 【优化 #8】通知渠道只创建一次
        if (!channelCreated && Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                NOTIFICATION_CHANNEL_ID,
                "OTA 升级下载",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "应用版本更新下载进度"
            }
            val notificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
            channelCreated = true
        }

        val notification = NotificationCompat.Builder(context, NOTIFICATION_CHANNEL_ID)
            .setSmallIcon(android.R.drawable.stat_sys_download)
            .setContentTitle("正在下载 $versionName")
            .setContentText(if (progress >= 0) "已完成 $progress%" else "正在下载...")
            .setProgress(100, progress.coerceAtLeast(0), progress < 0)
            .setOngoing(true)
            .setSilent(true)
            .build()

        return ForegroundInfo(NOTIFICATION_ID, notification)
    }
}
