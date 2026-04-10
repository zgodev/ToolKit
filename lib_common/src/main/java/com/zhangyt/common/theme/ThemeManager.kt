package com.zhangyt.common.theme

import android.app.Activity
import android.app.Application
import android.os.Bundle
import com.tencent.mmkv.MMKV
import com.zhangyt.common.R
import com.zhangyt.common.utils.AppManager

/**
 * 主题管理：支持多套主色方案，秒切无重启。
 *
 * 原理：
 * 1. 通过 Activity.setTheme(themeResId) 动态加载主题。
 * 2. 切换时重建当前 Activity 栈中所有 Activity（recreate）。
 * 3. 持久化当前主题到 MMKV。
 *
 * 使用：
 * ```
 * // 在 BaseActivity.super.onCreate 前调用（见 CommonApplication 的 lifecycle 回调）
 * ThemeManager.init(application)
 *
 * // 切换主题
 * ThemeManager.switch(ThemeStyle.RED)
 * ```
 */
object ThemeManager {

    private const val KEY_THEME = "app_theme_style"
    private val mmkv by lazy { MMKV.defaultMMKV() }

    /** 当前主题风格，默认蓝色 */
    var current: ThemeStyle = ThemeStyle.BLUE
        private set

    fun init(app: Application) {
        val saved = mmkv.decodeString(KEY_THEME, ThemeStyle.BLUE.name)!!
        current = runCatching { ThemeStyle.valueOf(saved) }.getOrDefault(ThemeStyle.BLUE)

        // 每个 Activity 创建时自动应用主题
        app.registerActivityLifecycleCallbacks(object : Application.ActivityLifecycleCallbacks {
            override fun onActivityPreCreated(activity: Activity, savedInstanceState: Bundle?) {
                activity.setTheme(current.themeRes)
            }
            override fun onActivityCreated(a: Activity, b: Bundle?) {}
            override fun onActivityStarted(a: Activity) {}
            override fun onActivityResumed(a: Activity) {}
            override fun onActivityPaused(a: Activity) {}
            override fun onActivityStopped(a: Activity) {}
            override fun onActivitySaveInstanceState(a: Activity, b: Bundle) {}
            override fun onActivityDestroyed(a: Activity) {}
        })
    }

    /** 切换主题：持久化 + 重建所有 Activity。 */
    fun switch(style: ThemeStyle) {
        if (style == current) return
        current = style
        mmkv.encode(KEY_THEME, style.name)
        AppManager.recreateAll()
    }
}

/**
 * 可选主题列表。可根据设计师给的色板继续新增。
 *
 * 对应资源见 lib_common/res/values/themes.xml
 */
enum class ThemeStyle(val themeRes: Int, val colorName: String) {
    BLUE(R.style.Common_Theme_Blue, "蓝色"),
    RED(R.style.Common_Theme_Red, "红色"),
    GREEN(R.style.Common_Theme_Green, "绿色"),
    PURPLE(R.style.Common_Theme_Purple, "紫色"),
    DARK(R.style.Common_Theme_Dark, "暗夜");
}
