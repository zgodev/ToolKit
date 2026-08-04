package com.zhangyt.module.mine.viewmodel

import com.zhangyt.common.language.Language
import com.zhangyt.common.theme.ThemeStyle

/**
 * @description: 描述我的页面可恢复用户信息与偏好设置状态
 * @author: zhangyt
 * @Date: 2026-08-04
 */
data class MineUiState(
    val nickname: String = "",
    val phone: String = "",
    val isLoggedIn: Boolean = false,
    val theme: ThemeStyle = ThemeStyle.BLUE,
    val language: Language = Language.SYSTEM,
)

/**
 * @description: 定义我的页面交由 Android 容器执行的一次性副作用
 * @author: zhangyt
 * @Date: 2026-08-04
 */
sealed interface MineUiEffect {

    /**
     * @description: 请求容器应用新的应用主题
     * @author: zhangyt
     * @Date: 2026-08-04
     */
    data class ApplyTheme(val theme: ThemeStyle) : MineUiEffect

    /**
     * @description: 请求容器应用新的应用语言
     * @author: zhangyt
     * @Date: 2026-08-04
     */
    data class ApplyLanguage(val language: Language) : MineUiEffect

    /**
     * @description: 请求容器打开 OTA 页面
     * @author: zhangyt
     * @Date: 2026-08-04
     */
    data object OpenOta : MineUiEffect

    /**
     * @description: 通知容器用户会话已经退出
     * @author: zhangyt
     * @Date: 2026-08-04
     */
    data object LoggedOut : MineUiEffect
}
