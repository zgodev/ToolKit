package com.zhangyt.module.mine.viewmodel

import androidx.lifecycle.ViewModel
import com.zhangyt.common.language.Language
import com.zhangyt.common.language.LanguageManager
import com.zhangyt.common.theme.ThemeManager
import com.zhangyt.common.theme.ThemeStyle
import com.zhangyt.common.user.UserManager
import com.zhangyt.core.model.UserInfo
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.MutableStateFlow

/**
 * @description: 持有我的页面状态并把主题、语言、导航等操作转换为一次性副作用
 * @author: zhangyt
 * @Date: 2026-08-04
 */
class MineViewModel(
    userProvider: () -> UserInfo? = UserManager::getUser,
    private val logoutAction: () -> Unit = UserManager::logout,
    initialTheme: ThemeStyle = ThemeManager.current,
    initialLanguage: Language = LanguageManager.current(),
) : ViewModel() {

    private val initialUser = userProvider()
    private val _uiState = MutableStateFlow(
        MineUiState(
            nickname = initialUser?.nickname.orEmpty(),
            phone = initialUser?.phone.orEmpty(),
            isLoggedIn = initialUser != null,
            theme = initialTheme,
            language = initialLanguage,
        )
    )
    val uiState: StateFlow<MineUiState> = _uiState.asStateFlow()

    private val _uiEffect = Channel<MineUiEffect>(Channel.BUFFERED)
    val uiEffect = _uiEffect.receiveAsFlow()

    fun selectTheme(theme: ThemeStyle) {
        if (theme == _uiState.value.theme) return
        _uiState.value = _uiState.value.copy(theme = theme)
        _uiEffect.trySend(MineUiEffect.ApplyTheme(theme))
    }

    fun selectLanguage(language: Language) {
        if (language == _uiState.value.language) return
        _uiState.value = _uiState.value.copy(language = language)
        _uiEffect.trySend(MineUiEffect.ApplyLanguage(language))
    }

    fun openOta() {
        _uiEffect.trySend(MineUiEffect.OpenOta)
    }

    fun logout() {
        logoutAction()
        _uiState.value = _uiState.value.copy(
            nickname = "",
            phone = "",
            isLoggedIn = false,
        )
        _uiEffect.trySend(MineUiEffect.LoggedOut)
    }
}
