package com.zhangyt.common

import android.app.Application
import android.content.Context
import androidx.multidex.MultiDex
import com.alibaba.android.arouter.launcher.ARouter
import com.blankj.utilcode.util.Utils
import com.elvishew.xlog.LogConfiguration
import com.elvishew.xlog.LogLevel
import com.elvishew.xlog.XLog
import com.elvishew.xlog.flattener.PatternFlattener
import com.elvishew.xlog.printer.AndroidPrinter
import com.elvishew.xlog.printer.file.FilePrinter
import com.elvishew.xlog.printer.file.backup.FileSizeBackupStrategy
import com.elvishew.xlog.printer.file.clean.FileLastModifiedCleanStrategy
import com.elvishew.xlog.printer.file.naming.DateFileNameGenerator
import com.tencent.mmkv.MMKV
import android.os.Handler
import android.os.Looper
import com.tencent.smtt.export.external.TbsCoreSettings
import com.tencent.smtt.sdk.QbSdk
import com.zhangyt.common.language.LanguageManager
import com.zhangyt.common.theme.ThemeManager
import com.zhangyt.common.utils.AppManager
import com.zhangyt.common.utils.AppStartup
import com.zhangyt.common.web.WebEngine
import com.zhangyt.common.web.WebEngineConfig
import com.zhangyt.common.web.WebViewPool
import java.io.File

/**
 * 所有子模块通用的 Application 基类。
 *
 * 使用方式：
 * ```
 * class App : CommonApplication() {
 *     override fun onCreate() {
 *         super.onCreate()
 *         // 在这里初始化项目特有的 SDK
 *     }
 * }
 * ```
 * 并在 app module 的 AndroidManifest.xml 中指定：
 * ```
 * <application android:name=".App" ... />
 * ```
 */
open class CommonApplication : Application() {

    companion object {
        @JvmStatic
        lateinit var instance: Application
            private set
    }

    override fun attachBaseContext(base: Context) {
        // MMKV 必须在 LanguageManager 之前初始化：
        // LanguageManager.attachBaseContext 会读取已保存的语言，依赖 MMKV。
        MMKV.initialize(base)

        // 多语言切换：Application 创建前应用语言
        val context = LanguageManager.attachBaseContext(base)
        super.attachBaseContext(context)
        MultiDex.install(this)
    }

    override fun onCreate() {
        super.onCreate()
        instance = this

        // ----- 主线程关键路径（顺序敏感：Utils → ARouter → AppManager → ThemeManager） -----
        AppStartup.mainInit { Utils.init(this) }

        AppStartup.mainInit {
            if (BuildConfig.DEBUG) {
                ARouter.openLog()
                ARouter.openDebug()
            }
            ARouter.init(this)
        }

        AppStartup.mainInit { registerActivityLifecycleCallbacks(AppManager) }

        // ThemeManager 读 MMKV 设置主题，首个 Activity 的 setContentView 前必须完成
        AppStartup.mainInit { ThemeManager.init(this) }

        // ----- 后台并行（非关键路径） -----
        AppStartup.asyncInit("xlog") { initXLog() }
        // WebViewPool：保存引用，原生预热延到首帧后（避免阻塞冷启动）
        WebViewPool.init(this)
        Handler(Looper.getMainLooper()).post {
            WebViewPool.onEngineReady(WebEngine.NATIVE)
        }

        // TBS X5：异步初始化，成功后再预热 X5 池
        AppStartup.asyncInit("tbs") { initTBS() }
    }

    /**
     * 初始化腾讯 TBS X5 内核。
     *
     * - 多线程初始化以加速首次启动
     * - 初始化成功后标记 [WebEngineConfig.isX5Initialized]
     * - 如果 X5 初始化失败，[WebEngineConfig.resolveEngine] 会自动降级到 NATIVE
     */
    private fun initTBS() {
        // 开启多线程优化，加速 X5 内核初始化
        val settings = mapOf(
            TbsCoreSettings.TBS_SETTINGS_USE_SPEEDY_CLASSLOADER to true,
            TbsCoreSettings.TBS_SETTINGS_USE_DEXLOADER_SERVICE to true,
        )
        QbSdk.initTbsSettings(settings)

        // 预初始化 X5 内核
        QbSdk.initX5Environment(this, object : QbSdk.PreInitCallback {
            override fun onCoreInitFinished() {
                // X5 内核初始化完成
            }

            override fun onViewInitFinished(isX5Core: Boolean) {
                // isX5Core: true 表示 X5 内核加载成功
                WebEngineConfig.isX5Initialized = isX5Core
                if (isX5Core) {
                    // 回调线程不确定，必须切主线程创建 WebView
                    Handler(Looper.getMainLooper()).post {
                        WebViewPool.onEngineReady(WebEngine.X5)
                    }
                }
            }
        })
    }

    /**
     * 初始化 XLog 日志框架。
     *
     * - Debug：ALL 级别，输出到 Logcat + 文件
     * - Release：WARN 级别，仅输出到文件
     *
     * FilePrinter 构造包含文件 IO，放到后台线程以减少冷启动阻塞；
     * 在 XLog.init 完成前的极少数早期日志会被 fallback 吞掉，这是可接受的权衡。
     */
    private fun initXLog() {
        val config = LogConfiguration.Builder()
            .logLevel(if (BuildConfig.DEBUG) LogLevel.ALL else LogLevel.WARN)
            .tag("ToolKit")
            .enableThreadInfo()
            // 5 级栈深在高频日志下开销较大；2 级足够定位到调用方
            .enableStackTrace(2)
            .build()

        val androidPrinter = AndroidPrinter(true)

        val externalDir = getExternalFilesDir(null)
        val filePrinter = externalDir?.let {
            val logDir = File(it, "logs").absolutePath
            FilePrinter.Builder(logDir)
                .fileNameGenerator(DateFileNameGenerator())
                .backupStrategy(FileSizeBackupStrategy(5 * 1024 * 1024))        // 单文件最大 5MB
                .cleanStrategy(FileLastModifiedCleanStrategy(7L * 24 * 3600_000)) // 保留 7 天
                .flattener(PatternFlattener("{d yyyy-MM-dd HH:mm:ss.SSS} {l}/{t}: {m}"))
                .build()
        }

        if (BuildConfig.DEBUG) {
            if (filePrinter != null) XLog.init(config, androidPrinter, filePrinter)
            else XLog.init(config, androidPrinter)
        } else {
            if (filePrinter != null) XLog.init(config, filePrinter)
            else XLog.init(config, androidPrinter)  // 外部存储不可用时降级到 Logcat
        }
    }
}
