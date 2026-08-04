package com.zhangyt.common.base

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zhangyt.common.state.UiLoadState
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel 基类（MVVM / MVI 通用）。
 *
 * 同时提供两套加载状态 API，业务可二选一：
 *
 * 1. **LiveData 版本** —— 老接口，保持兼容
 *    - [loadState]：`MutableLiveData<UiLoadState>`
 *    - [launch]：协程 + 统一异常处理，自动推送 `Loading → Success/Error`
 *
 * 2. **StateFlow 版本** —— 推荐新写法
 *    - [loadStateFlow]：只读 `StateFlow<UiLoadState>`，初始为 [UiLoadState.Idle]
 *    - [launchFlow]：与 `launch` 行为一致，通过 StateFlow 分发状态
 *
 * 语义说明：`launch` / `launchFlow` 推送的 `Success` 表示"协程块执行完毕"，不等同于
 * 业务数据就位。如果 block 内 `collect { flow }` 多次发射，`Success` 会在 `collect`
 * 终止后才发出。业务数据建议通过自己的 `LiveData<T>` / `StateFlow<T>` 分发，
 * 仅用 `loadState*` 控制 Loading/Error 的 UI 表现。
 *
 * --- 示例：StateFlow 版 MVVM ---
 * ```
 * @HiltViewModel
 * class UserProfileViewModel @Inject constructor(
 *     private val repo: UserRepository
 * ) : BaseViewModel() {
 *
 *     private val _user = MutableStateFlow<UserInfo?>(null)
 *     val user: StateFlow<UserInfo?> = _user.asStateFlow()
 *
 *     fun loadUser(id: String) = launchFlow(showLoading = true) {
 *         repo.getUser(id).collect { _user.value = it }
 *     }
 * }
 * ```
 *
 * Activity / Fragment 侧（配合 `repeatOnLifecycle`）：
 * ```
 * override fun observeViewModel() {
 *     lifecycleScope.launch {
 *         repeatOnLifecycle(Lifecycle.State.STARTED) {
 *             launch {
 *                 viewModel.loadStateFlow.collect { state ->
 *                     when (state) {
 *                         is UiLoadState.Loading -> showLoading()
 *                         is UiLoadState.Error   -> { hideLoading(); toast(state.message) }
 *                         else                   -> hideLoading()
 *                     }
 *                 }
 *             }
 *             launch {
 *                 viewModel.user.collect { it?.let { binding.tvName.text = it.nickname } }
 *             }
 *         }
 *     }
 * }
 * ```
 */
open class BaseViewModel : ViewModel() {

    // ---------- LiveData 版本（保留兼容） ----------

    /** 统一 UI 状态，Activity / Fragment 通过 observe 显示 Loading / 错误等。 */
    val loadState = MutableLiveData<UiLoadState>()

    /**
     * 启动协程，自动捕获异常并推送到 [loadState]。
     * @param showLoading 是否展示 Loading
     * @param block       业务代码
     */
    protected fun launch(
        showLoading: Boolean = false,
        onError: ((Throwable) -> Unit)? = null,
        block: suspend CoroutineScope.() -> Unit
    ): Job = viewModelScope.launch {
        try {
            if (showLoading) loadState.postValue(UiLoadState.Loading)
            block()
            loadState.postValue(UiLoadState.Success)
        } catch (cancellation: CancellationException) {
            throw cancellation
        } catch (t: Throwable) {
            loadState.postValue(UiLoadState.Error(t.message ?: "未知错误", t))
            onError?.invoke(t)
        }
    }

    // ---------- StateFlow 版本（推荐） ----------

    private val _loadStateFlow = MutableStateFlow<UiLoadState>(UiLoadState.Idle)

    /** 只读 StateFlow。使用 `repeatOnLifecycle` 订阅。 */
    val loadStateFlow: StateFlow<UiLoadState> = _loadStateFlow.asStateFlow()

    /**
     * 启动协程，异常自动推送到 [loadStateFlow]。与 [launch] 相比：
     * - 用 StateFlow 分发状态（支持 lifecycle-aware `repeatOnLifecycle`）
     * - 初始态 `Idle` 可用于区分"未执行" vs "已执行成功"
     */
    protected fun launchFlow(
        showLoading: Boolean = false,
        onError: ((Throwable) -> Unit)? = null,
        block: suspend CoroutineScope.() -> Unit
    ): Job = viewModelScope.launch {
        try {
            if (showLoading) _loadStateFlow.value = UiLoadState.Loading
            block()
            _loadStateFlow.value = UiLoadState.Success
        } catch (cancellation: CancellationException) {
            throw cancellation
        } catch (t: Throwable) {
            _loadStateFlow.value = UiLoadState.Error(t.message ?: "未知错误", t)
            onError?.invoke(t)
        }
    }

    /** 显式把 [loadStateFlow] 重置为 [UiLoadState.Idle]，常用于页面复用/重新进入。 */
    protected fun resetLoadState() {
        _loadStateFlow.value = UiLoadState.Idle
    }
}
