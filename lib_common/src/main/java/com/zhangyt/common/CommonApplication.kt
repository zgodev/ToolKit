package com.zhangyt.common

import android.app.Application
import android.content.Context
import androidx.multidex.MultiDex
import com.alibaba.android.arouter.launcher.ARouter
import com.blankj.utilcode.util.Utils
import com.tencent.mmkv.MMKV
import com.zhangyt.common.language.LanguageManager
import com.zhangyt.common.theme.ThemeManager
import com.zhangyt.common.utils.AppManager
import com.elvishew.xlog.LogConfiguration
import com.elvishew.xlog.LogLevel
import com.elvishew.xlog.XLog
import com.elvishew.xlog.printer.AndroidPrinter
import com.elvishew.xlog.printer.file.FilePrinter
import com.elvishew.xlog.printer.file.backup.FileSizeBackupStrategy
import com.elvishew.xlog.printer.file.clean.FileLastModifiedCleanStrategy
import com.elvishew.xlog.printer.file.naming.DateFileNameGenerator
import com.elvishew.xlog.flattener.PatternFlattener
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

        // 工具类初始化
        Utils.init(this)

        // ARouter 初始化（release 包请移除 debug 日志）
        if (BuildConfig.DEBUG) {
            ARouter.openLog()
            ARouter.openDebug()
        }
        ARouter.init(this)

        // Activity 栈管理
        registerActivityLifecycleCallbacks(AppManager)

        // 主题初始化
        ThemeManager.init(this)

        // XLog 日志框架初始化
        initXLog()
        try {
            throw NullPointerException()
        } catch (e: Exception) {
            XLog.tag("TAG").e(e)
        }
    }

    /**
     * 初始化 XLog 日志框架
     *
     * - Debug 模式：ALL 级别，输出到 Logcat + 文件
     * - Release 模式：WARN 级别，仅输出到文件（方便线上排查）
     */
    private fun initXLog() {
        val config = LogConfiguration.Builder()
            .logLevel(if (BuildConfig.DEBUG) LogLevel.ALL else LogLevel.WARN)
            .tag("ToolKit")
            .enableThreadInfo()
            .enableStackTrace(5)
            .build()

        // Logcat 打印器
        val androidPrinter = AndroidPrinter(true)

        // 文件打印器：日志写入文件，方便排查线上问题
        // getExternalFilesDir 可能返回 null（外部存储不可用），此时仅用 Logcat
        val externalDir = getExternalFilesDir(null)
        val filePrinter = externalDir?.let {
            val logDir = File(it, "logs").absolutePath
            FilePrinter.Builder(logDir)
                .fileNameGenerator(DateFileNameGenerator())
                .backupStrategy(FileSizeBackupStrategy(5 * 1024 * 1024))        // 单文件最大 5MB
                .cleanStrategy(FileLastModifiedCleanStrategy(7L * 24 * 3600_000)) // 保留 7 天
                .flattener(PatternFlattener("{d yyyy-MM-dd HH:mm:ss.SSS} {l}/{t}: {m}")) // 自定义时间格式
                .build()
        }

        if (BuildConfig.DEBUG) {
            if (filePrinter != null) {
                XLog.init(config, androidPrinter, filePrinter)
            } else {
                XLog.init(config, androidPrinter)
            }
        } else {
            if (filePrinter != null) {
                XLog.init(config, filePrinter)
            } else {
                // 外部存储不可用，降级到 Logcat
                XLog.init(config, androidPrinter)
            }
        }
    }
}
