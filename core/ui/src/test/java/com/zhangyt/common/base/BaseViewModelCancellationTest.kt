package com.zhangyt.common.base

import com.zhangyt.common.state.UiLoadState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.awaitCancellation
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test

/**
 * @description: 验证 ViewModel 协程取消不会被转换为普通页面错误
 * @author: zhangyt
 * @Date: 2026-08-04
 */
@OptIn(ExperimentalCoroutinesApi::class)
class BaseViewModelCancellationTest {

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
    fun `cancellation is not exposed as load error`() = runTest(dispatcher) {
        val viewModel = CancellationViewModel()
        val job = viewModel.startLongRunningTask()

        dispatcher.scheduler.runCurrent()
        job.cancel()
        job.join()

        assertFalse(viewModel.loadStateFlow.value is UiLoadState.Error)
    }

    @Test
    fun `legacy launch does not report cancellation to error callback`() = runTest(dispatcher) {
        var reportedError: Throwable? = null
        val viewModel = CancellationViewModel()
        val job = viewModel.startLegacyTask { reportedError = it }

        dispatcher.scheduler.runCurrent()
        job.cancel()
        job.join()

        assertNull(reportedError)
    }

    /**
     * @description: 暴露 BaseViewModel 受保护启动入口以验证取消传播
     * @author: zhangyt
     * @Date: 2026-08-04
     */
    private class CancellationViewModel : BaseViewModel() {
        fun startLongRunningTask() = launchFlow {
            awaitCancellation()
        }

        fun startLegacyTask(onError: (Throwable) -> Unit) = launch(onError = onError) {
            awaitCancellation()
        }
    }
}
