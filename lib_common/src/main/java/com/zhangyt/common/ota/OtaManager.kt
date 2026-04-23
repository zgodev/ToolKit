package com.zhangyt.common.ota

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.util.Log
import androidx.core.content.FileProvider
import androidx.lifecycle.LiveData
import androidx.work.*
import java.io.File
import java.util.concurrent.TimeUnit

/**
 * OTA 管理器
 *
 * 封装 WorkManager 下载任务的创建、观察和安装逻辑。
 *
 * 使用示例：
 * ```
 * val otaManager = OtaManager(context)
 *
 * // 1. 启动下载
 * otaManager.startDownload(otaInfo)
 *
 * // 2. 观察下载状态
 * otaManager.getWorkInfoLiveData().observe(this) { workInfo ->
 *     when (workInfo?.state) {
 *         WorkInfo.State.RUNNING -> { /* 获取进度 */ }
 *         WorkInfo.State.SUCCEEDED -> { /* 安装 APK */ }
 *     }
 * }
 *
 * // 3. 取消下载
 * otaManager.cancelDownload()
 * ```
 */
class OtaManager(private val context: Context) {

    companion object {
        private const val TAG = "OtaManager"
        const val OTA_WORK_NAME = "ota_download_work"
    }

    private val workManager: WorkManager = WorkManager.getInstance(context)

    /**
     * 当前是否有正在进行的下载任务（ENQUEUED 或 RUNNING）
     *
     * 【注意】内部使用 ListenableFuture.get() 同步阻塞，请勿在主线程调用，
     * 或使用 [isDownloadingAsync] 异步版本。
     *
     * @return true = 有活跃任务；false = 无任务或已终态
     */
    fun isDownloading(): Boolean {
        return try {
            val workInfos = workManager.getWorkInfosForUniqueWork(OTA_WORK_NAME).get()
            workInfos.any { !it.state.isFinished }
        } catch (e: Exception) {
            Log.w(TAG, "查询任务状态失败: ${e.message}")
            false
        }
    }

    /**
     * 异步查询是否有正在进行的下载任务，通过回调返回结果（主线程安全）
     */
    fun isDownloadingAsync(callback: (Boolean) -> Unit) {
        val future = workManager.getWorkInfosForUniqueWork(OTA_WORK_NAME)
        future.addListener(
            {
                try {
                    val workInfos = future.get()
                    callback(workInfos.any { !it.state.isFinished })
                } catch (e: Exception) {
                    Log.w(TAG, "异步查询任务状态失败: ${e.message}")
                    callback(false)
                }
            },
            { runnable -> runnable.run() }
        )
    }

    /**
     * 启动 OTA 下载任务（异步版本，主线程安全）
     *
     * 策略说明：
     * - 使用 [ExistingWorkPolicy.KEEP]：如果已有同名任务正在运行/排队，
     *   则保留旧任务，忽略本次请求，避免重复下载浪费流量。
     * - 如果需要强制用新版本覆盖旧任务，调用 [forceStartDownload]。
     *
     * @param otaInfo  OTA 升级信息
     * @param callback 回调：true = 成功入队；false = 已有任务在执行，本次被忽略
     */
    fun startDownload(otaInfo: OtaInfo, callback: (Boolean) -> Unit) {
        Log.d(TAG, "请求下载: ${otaInfo.downloadUrl}")

        isDownloadingAsync { downloading ->
            if (downloading) {
                Log.d(TAG, "已有下载任务在执行，本次请求被忽略")
                callback(false)
            } else {
                enqueueWork(otaInfo, ExistingWorkPolicy.KEEP)
                callback(true)
            }
        }
    }

    /**
     * 强制启动下载（取消旧任务，清除临时文件，从头下载）
     *
     * 适用场景：服务端返回了更新版本，需要覆盖当前正在下载的旧版本。
     *
     * @param otaInfo OTA 升级信息
     */
    fun forceStartDownload(otaInfo: OtaInfo) {
        Log.d(TAG, "强制下载（覆盖旧任务）: ${otaInfo.downloadUrl}")
        // 清除旧的临时文件，防止新任务误续传旧版本的数据
        cleanTempFiles(otaInfo.versionName)
        enqueueWork(otaInfo, ExistingWorkPolicy.REPLACE)
    }

    /**
     * 清除指定版本的临时下载文件
     */
    private fun cleanTempFiles(versionName: String) {
        try {
            val downloadDir = File(
                context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS),
                "ota"
            )
            val tempFile = File(downloadDir, "update_${versionName}.apk.tmp")
            val apkFile = File(downloadDir, "update_${versionName}.apk")
            if (tempFile.exists()) {
                tempFile.delete()
                Log.d(TAG, "已清除临时文件: ${tempFile.name}")
            }
            if (apkFile.exists()) {
                apkFile.delete()
                Log.d(TAG, "已清除旧 APK: ${apkFile.name}")
            }
        } catch (e: Exception) {
            Log.w(TAG, "清除临时文件失败: ${e.message}")
        }
    }

    /**
     * 内部方法：构建并入队 WorkRequest
     */
    private fun enqueueWork(otaInfo: OtaInfo, policy: ExistingWorkPolicy) {
        val inputData = Data.Builder()
            .putString(OtaWorker.KEY_DOWNLOAD_URL, otaInfo.downloadUrl)
            .putString(OtaWorker.KEY_FILE_MD5, otaInfo.md5)
            .putString(OtaWorker.KEY_VERSION_NAME, otaInfo.versionName)
            .putLong(OtaWorker.KEY_FILE_SIZE, otaInfo.fileSize)
            .build()

        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .setRequiresStorageNotLow(true)
            .build()

        val workRequest = OneTimeWorkRequestBuilder<OtaWorker>()
            .setInputData(inputData)
            .setConstraints(constraints)
            .addTag(OTA_WORK_NAME)
            .setBackoffCriteria(
                BackoffPolicy.LINEAR,
                30,
                TimeUnit.SECONDS
            )
            .build()

        workManager.enqueueUniqueWork(OTA_WORK_NAME, policy, workRequest)
        Log.d(TAG, "下载任务已入队: ${workRequest.id}, 策略: $policy")
    }

    /**
     * 获取下载任务的 LiveData（用于 UI 观察状态和进度）
     *
     * 使用 UniqueWork 名称查询，确保只返回当前唯一任务的状态，
     * 避免 Tag 查询返回历史所有任务导致状态混乱。
     */
    fun getWorkInfoLiveData(): LiveData<List<WorkInfo>> {
        return workManager.getWorkInfosForUniqueWorkLiveData(OTA_WORK_NAME)
    }

    /**
     * 取消下载任务
     */
    fun cancelDownload() {
        Log.d(TAG, "取消下载任务")
        workManager.cancelUniqueWork(OTA_WORK_NAME)
    }

    /**
     * 安装 APK
     *
     * @param filePath APK 文件路径
     */
    fun installApk(filePath: String) {
        val file = File(filePath)
        if (!file.exists()) {
            Log.e(TAG, "APK 文件不存在: $filePath")
            return
        }

        val intent = Intent(Intent.ACTION_VIEW).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                // Android 7.0+ 使用 FileProvider
                val uri = FileProvider.getUriForFile(
                    context,
                    "${context.packageName}.fileprovider",
                    file
                )
                setDataAndType(uri, "application/vnd.android.package-archive")
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            } else {
                setDataAndType(
                    Uri.fromFile(file),
                    "application/vnd.android.package-archive"
                )
            }
        }

        context.startActivity(intent)
    }
}
