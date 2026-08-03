package com.zhangyt.common.language

import android.content.Context
import android.content.res.Configuration
import android.os.Build
import com.tencent.mmkv.MMKV
import com.zhangyt.common.utils.AppManager
import java.util.Locale

/**
 * 多语言管理。
 *
 * 原理：在 Application / Activity 的 attachBaseContext(context) 中
 * 通过 Configuration#setLocale 强制使用持久化的语言。
 *
 * 使用：
 * ```
 * // 切换到英文
 * LanguageManager.switch(activity, Language.EN)
 *
 * // 切换到简体中文
 * LanguageManager.switch(activity, Language.ZH_CN)
 * ```
 * 注意：切换后会 recreate 当前 Activity 栈。
 *
 * 在 BaseActivity 可以按需再重写 attachBaseContext 以获得更严谨的效果（兼容 Android 8.0+）。
 */
object LanguageManager {

    private const val KEY_LANGUAGE = "app_language"
    private val mmkv by lazy { MMKV.defaultMMKV() }

    /** 获取当前已保存的语言。 */
    fun current(): Language {
        val code = mmkv.decodeString(KEY_LANGUAGE, Language.SYSTEM.code)!!
        return Language.values().firstOrNull { it.code == code } ?: Language.SYSTEM
    }

    /** 切换语言。 */
    fun switch(context: Context, lang: Language) {
        mmkv.encode(KEY_LANGUAGE, lang.code)
        updateResources(context, lang)
        AppManager.recreateAll()
    }

    /** 在 Application/Activity 的 attachBaseContext 中调用。 */
    fun attachBaseContext(context: Context): Context {
        return updateResources(context, current())
    }

    private fun updateResources(context: Context, lang: Language): Context {
        val locale = if (lang == Language.SYSTEM) getSystemLocale() else Locale(lang.code)
        Locale.setDefault(locale)
        val res = context.resources
        val config = Configuration(res.configuration)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            config.setLocale(locale)
            return context.createConfigurationContext(config)
        }
        @Suppress("DEPRECATION")
        config.locale = locale
        @Suppress("DEPRECATION")
        res.updateConfiguration(config, res.displayMetrics)
        return context
    }

    private fun getSystemLocale(): Locale {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            Resources_getSystem_locale0()
        } else {
            @Suppress("DEPRECATION")
            android.content.res.Resources.getSystem().configuration.locale
        }
    }

    private fun Resources_getSystem_locale0(): Locale {
        return android.content.res.Resources.getSystem().configuration.locales[0]
    }
}

/** 支持的语言枚举。需要新增时在此 + values-xx 目录下添加资源。 */
enum class Language(val code: String, val displayName: String) {
    SYSTEM("", "跟随系统"),
    ZH_CN("zh", "简体中文"),
    EN("en", "English"),
    JA("ja", "日本語");
}
