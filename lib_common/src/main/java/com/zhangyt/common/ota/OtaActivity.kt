package com.zhangyt.common.ota

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.work.WorkInfo
import com.alibaba.android.arouter.facade.annotation.Route
import com.zhangyt.common.base.BaseActivity
import com.zhangyt.common.databinding.TestActivityOtaBinding
import com.zhangyt.common.router.RouterPath

/**
 * OTA 升级页面
 *
 * 功能：
 * 1. 检查版本更新
 * 2. 后台下载 APK（WorkManager）
 * 3. 实时显示下载进度
 * 4. 下载完成后自动安装
 */
@Route(path = RouterPath.Mine.ACTIVITY_OTA)
class OtaActivity : BaseActivity<TestActivityOtaBinding>() {

    private lateinit var otaManager: OtaManager

    /**
     * 【Bug #2 修复】标记 SUCCEEDED 是否已处理过，防止 Activity 重建后重复触发安装。
     * WorkManager 的 LiveData 在 Activity 重建时会重新发送最新状态，
     * 如果不加标记，每次 onResume 都会再次调用 installApk。
     */
    private var installTriggered = false

    /** 模拟的 OTA 升级信息（实际项目中从服务端获取） */
    private val mockOtaInfo = OtaInfo(
        versionCode = 2,
        versionName = "1.1.0",
        downloadUrl = "https://turing-appstore.oss-cn-beijing.aliyuncs.com/zhiwa_launcher/HuangChuan/AIRoom_Huangchuan_V1.4.3_43_20260417_1621_22.apk",
        fileSize = 313928866, // 30MB
        md5 = "199626c78176f7d9ab4d1d256e082fae",
        updateLog = "1. 新增 OTA 升级功能\n2. 修复已知 Bug\n3. 优化性能体验",
        forceUpdate = false
    )

    companion object {
        private const val REQUEST_CODE_STORAGE = 1001
    }

    /**
     * 【Bug #9 / #5 修复】使用 ActivityResultLauncher 替代已过时的 startActivityForResult，
     * 并在用户从安装权限设置页返回后自动检查权限并继续下载。
     */
    private val installPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        // 从"安装未知来源"设置页返回后，重新检查权限
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && packageManager.canRequestPackageInstalls()) {
            startOtaDownload()
        } else {
            Toast.makeText(this, "未授予安装权限，无法更新", Toast.LENGTH_SHORT).show()
        }
    }

    override fun getViewBinding() = TestActivityOtaBinding.inflate(layoutInflater)

    override fun initView() {
        otaManager = OtaManager(this)

        // 显示更新信息
        binding.tvVersionName.text = "最新版本：v${mockOtaInfo.versionName}"
        binding.tvUpdateLog.text = mockOtaInfo.updateLog
        binding.tvFileSize.text = "安装包大小：${formatFileSize(mockOtaInfo.fileSize)}"

        // 开始下载按钮
        binding.btnStartDownload.setOnClickListener {
            if (checkPermissions()) {
                startOtaDownload()
            }
        }

        // 取消下载按钮
        binding.btnCancelDownload.setOnClickListener {
            otaManager.cancelDownload()
            resetUI()
            Toast.makeText(this, "已取消下载", Toast.LENGTH_SHORT).show()
        }

        // 返回按钮
        binding.ivBack.setOnClickListener { finish() }
    }

    override fun observeViewModel() {
        /**
         * 【Bug #4 修复】使用 getWorkInfosForUniqueWorkLiveData 替代 getWorkInfosByTagLiveData。
         * Tag 版本会返回所有历史任务（包括已完成的旧任务），
         * UniqueWork 版本只返回当前唯一任务的状态，更加精确。
         */
        otaManager.getWorkInfoLiveData().observe(this) { workInfos ->
            // 取最新的未终结任务，若没有则取最后一个（终态）
            val workInfo = workInfos?.firstOrNull { !it.state.isFinished }
                ?: workInfos?.lastOrNull()
                ?: return@observe

            when (workInfo.state) {
                WorkInfo.State.ENQUEUED -> {
                    binding.tvStatus.text = "等待下载..."
                    binding.btnStartDownload.isEnabled = false
                    binding.btnCancelDownload.isEnabled = true
                }

                WorkInfo.State.RUNNING -> {
                    val progress = workInfo.progress.getInt(OtaWorker.KEY_PROGRESS, 0)
                    binding.progressBar.progress = progress
                    binding.tvProgress.text = "$progress%"
                    binding.tvStatus.text = "正在下载..."
                    binding.btnStartDownload.isEnabled = false
                    binding.btnCancelDownload.isEnabled = true
                }

                WorkInfo.State.SUCCEEDED -> {
                    binding.progressBar.progress = 100
                    binding.tvProgress.text = "100%"
                    binding.tvStatus.text = "下载完成，准备安装..."
                    binding.btnStartDownload.text = "重新下载"
                    binding.btnStartDownload.isEnabled = true
                    binding.btnCancelDownload.isEnabled = false

                    // 【Bug #2 修复】只触发一次安装
                    if (!installTriggered) {
                        installTriggered = true
                        val filePath = workInfo.outputData.getString(OtaWorker.KEY_FILE_PATH)
                        if (!filePath.isNullOrBlank()) {
                            otaManager.installApk(filePath)
                        }
                    }
                }

                WorkInfo.State.FAILED -> {
                    val error = workInfo.outputData.getString("error") ?: "未知错误"
                    binding.tvStatus.text = "下载失败：$error"
                    binding.btnStartDownload.text = "重试"
                    binding.btnStartDownload.isEnabled = true
                    binding.btnCancelDownload.isEnabled = false
                    Toast.makeText(this, "下载失败：$error", Toast.LENGTH_SHORT).show()
                }

                WorkInfo.State.CANCELLED -> {
                    resetUI()
                }

                WorkInfo.State.BLOCKED -> {
                    binding.tvStatus.text = "任务被阻塞，等待条件满足..."
                }
            }
        }
    }

    /**
     * 开始 OTA 下载
     *
     * 如果已有任务正在下载，弹窗让用户选择：
     * - 继续当前下载（不做任何操作）
     * - 取消旧任务，重新下载
     */
    private fun startOtaDownload() {
        // 重置安装标记（用户重新触发了下载）
        installTriggered = false

        // 【Bug #3 修复】使用异步版本，避免主线程阻塞
        otaManager.startDownload(mockOtaInfo) { enqueued ->
            runOnUiThread {
                if (enqueued) {
                    binding.tvStatus.text = "开始下载..."
                    binding.progressBar.progress = 0
                    binding.tvProgress.text = "0%"
                    binding.btnStartDownload.isEnabled = false
                    binding.btnStartDownload.text = "立即更新"
                    binding.btnCancelDownload.isEnabled = true
                } else {
                    showDuplicateDownloadDialog()
                }
            }
        }
    }

    /**
     * 弹窗提示：已有下载任务正在进行
     */
    private fun showDuplicateDownloadDialog() {
        AlertDialog.Builder(this)
            .setTitle("提示")
            .setMessage("当前已有下载任务正在进行，是否取消当前任务并重新下载？")
            .setPositiveButton("重新下载") { _, _ ->
                installTriggered = false
                otaManager.forceStartDownload(mockOtaInfo)
                binding.tvStatus.text = "重新开始下载..."
                binding.progressBar.progress = 0
                binding.tvProgress.text = "0%"
                binding.btnStartDownload.isEnabled = false
                binding.btnStartDownload.text = "立即更新"
                binding.btnCancelDownload.isEnabled = true
            }
            .setNegativeButton("继续当前下载", null)
            .setCancelable(true)
            .show()
    }

    /**
     * 重置 UI 到初始状态
     */
    private fun resetUI() {
        binding.progressBar.progress = 0
        binding.tvProgress.text = "0%"
        binding.tvStatus.text = "点击下方按钮开始下载"
        binding.btnStartDownload.text = "立即更新"
        binding.btnStartDownload.isEnabled = true
        binding.btnCancelDownload.isEnabled = false
    }

    /**
     * 检查所需权限
     */
    private fun checkPermissions(): Boolean {
        val permissions = mutableListOf<String>()

        // Android 10 以下需要存储权限（10+ 使用 Scoped Storage，不再需要）
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED
            ) {
                permissions.add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
            }
        }

        // Android 8.0+ 需要安装未知来源权限
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (!packageManager.canRequestPackageInstalls()) {
                Toast.makeText(this, "请在设置中允许安装未知来源应用", Toast.LENGTH_LONG).show()
                // 【Bug #5/#9 修复】使用 ActivityResultLauncher，返回后自动处理
                val intent = Intent(
                    Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES,
                    Uri.parse("package:$packageName")
                )
                installPermissionLauncher.launch(intent)
                return false
            }
        }

        if (permissions.isNotEmpty()) {
            ActivityCompat.requestPermissions(
                this,
                permissions.toTypedArray(),
                REQUEST_CODE_STORAGE
            )
            return false
        }

        return true
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_STORAGE) {
            if (grantResults.all { it == PackageManager.PERMISSION_GRANTED }) {
                startOtaDownload()
            } else {
                Toast.makeText(this, "需要存储权限才能下载更新", Toast.LENGTH_SHORT).show()
            }
        }
    }

    /**
     * 格式化文件大小
     */
    private fun formatFileSize(size: Long): String {
        return when {
            size >= 1024 * 1024 * 1024 -> "%.2f GB".format(size / (1024.0 * 1024 * 1024))
            size >= 1024 * 1024 -> "%.2f MB".format(size / (1024.0 * 1024))
            size >= 1024 -> "%.2f KB".format(size / 1024.0)
            else -> "$size B"
        }
    }
}
