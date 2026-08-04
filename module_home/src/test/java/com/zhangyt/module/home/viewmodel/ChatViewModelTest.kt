package com.zhangyt.module.home.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test

/**
 * @description: 验证首页消息状态加载的数量、顺序和未读信息
 * @author: zhangyt
 * @Date: 2026-08-04
 */
@OptIn(ExperimentalCoroutinesApi::class)
class ChatViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private val dispatcher = StandardTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(dispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `loading messages publishes the expected conversation summary`() = runTest(dispatcher) {
        val viewModel = ChatViewModel()

        viewModel.loadMessages()
        advanceUntilIdle()

        val messages = requireNotNull(viewModel.messages.value)
        assertEquals(listOf("1", "2", "3", "4", "5"), messages.map { it.id })
        assertEquals(12, messages.sumOf { it.unreadCount })
    }
}
