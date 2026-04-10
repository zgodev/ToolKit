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
    }
}
