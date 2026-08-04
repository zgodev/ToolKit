package com.zhangyt.module.mine.viewmodel

import com.zhangyt.common.language.Language
import com.zhangyt.common.theme.ThemeStyle
import com.zhangyt.core.model.UserInfo
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.CoroutineStart
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * @description: 验证我的页面状态初始化、设置选择和退出登录行为
 * @author: zhangyt
 * @Date: 2026-08-04
 */
class MineViewModelTest {

    private val user = UserInfo(
        userId = "42",
        nickname = "张三",
        phone = "13800000000",
        token = "token",
    )

    @Test
    fun `initial state reflects current user and preferences`() {
        val viewModel = createViewModel()

        assertEquals("张三", viewModel.uiState.value.nickname)
        assertEquals("13800000000", viewModel.uiState.value.phone)
        assertEquals(ThemeStyle.BLUE, viewModel.uiState.value.theme)
        assertEquals(Language.ZH_CN, viewModel.uiState.value.language)
        assertTrue(viewModel.uiState.value.isLoggedIn)
    }

    @Test
    fun `selecting a new theme updates state and emits apply effect`() = runBlocking {
        val viewModel = createViewModel()
        val effect = async(start = CoroutineStart.UNDISPATCHED) {
            viewModel.uiEffect.first()
        }

        viewModel.selectTheme(ThemeStyle.RED)

        assertEquals(ThemeStyle.RED, viewModel.uiState.value.theme)
        assertEquals(MineUiEffect.ApplyTheme(ThemeStyle.RED), effect.await())
    }

    @Test
    fun `logout clears session and emits completion effect`() = runBlocking {
        var sessionCleared = false
        val viewModel = createViewModel(logoutAction = { sessionCleared = true })
        val effect = async(start = CoroutineStart.UNDISPATCHED) {
            viewModel.uiEffect.first()
        }

        viewModel.logout()

        assertTrue(sessionCleared)
        assertEquals(MineUiEffect.LoggedOut, effect.await())
    }

    private fun createViewModel(
        logoutAction: () -> Unit = {},
    ) = MineViewModel(
        userProvider = { user },
        logoutAction = logoutAction,
        initialTheme = ThemeStyle.BLUE,
        initialLanguage = Language.ZH_CN,
    )
}
