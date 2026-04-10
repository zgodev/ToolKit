package com.zhangyt.utils

import android.app.Activity
import android.os.Build
import android.view.View
import android.view.WindowManager
import androidx.annotation.ColorInt

/**
 * 状态栏工具类。
 *
 * 示例：
 * ```
 * StatusBarUtils.setColor(activity, Color.WHITE)
 * StatusBarUtils.setDarkMode(activity, true)   // 状态栏字体变黑
 * StatusBarUtils.setTranslucent(activity)      // 沉浸式
 * ```
 */
object StatusBarUtils {

    fun setColor(activity: Activity, @ColorInt color: Int) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val window = activity.window
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
            window.statusBarColor = color
        }
    }

    /** 深色模式（true 则字体变黑，适配白底）。 */
    fun setDarkMode(activity: Activity, dark: Boolean) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val decor = activity.window.decorView
            var flag = decor.systemUiVisibility
            flag = if (dark) flag or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            else flag and View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR.inv()
            decor.systemUiVisibility = flag
        }
    }

    fun setTranslucent(activity: Activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val window = activity.window
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.statusBarColor = 0
            window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN)
        }
    }
}
