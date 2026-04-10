package com.zhangyt.common.mvi

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

/**
 * MVI ViewModel 基类。
 *
 * 用法：
 * ```
 * class LoginMviViewModel : MviViewModel<LoginState, LoginIntent, LoginEffect>() {
 *
 *     override fun initialState() = LoginState()
 *
 *     override fun handleIntent(intent: LoginIntent) {
 *         when (intent) {
 *             is LoginIntent.Submit -> login(intent.name, intent.pwd)
 *         }
 *     }
 *
 *     private fun login(n: String, p: String) = viewModelScope.launch {
 *         updateState { copy(loading = true) }
 *         runCatching { repo.login(n, p) }
 *             .onSuccess {
 *                 updateState { copy(loading = false, user = it) }
 *                 sendEffect(LoginEffect.NavigateHome)
 *             }
 *             .onFailure { updateState { copy(loading = false, error = it.message) } }
 *     }
 * }
 * ```
 * 页面层使用 `viewModel.uiState.collect { render(it) }` 订阅状态，
 * 使用 `viewModel.uiEffect.collect { handle(it) }` 订阅一次性事件。
 */
abstract class MviViewModel<S : IUiState, I : IUiIntent, E : IUiEffect> : ViewModel() {

    private val _uiState: MutableStateFlow<S> by lazy { MutableStateFlow(initialState()) }
    val uiState: StateFlow<S> get() = _uiState.asStateFlow()

    private val _uiEffect = Channel<E>(Channel.BUFFERED)
    val uiEffect = _uiEffect.receiveAsFlow()

    private val _intents = MutableSharedFlow<I>(extraBufferCapacity = 64)

    init {
        _intents
            .onEach { handleIntent(it) }
            .launchIn(viewModelScope)
    }

    /** 分发用户意图。 */
    fun dispatch(intent: I) {
        viewModelScope.launch { _intents.emit(intent) }
    }

    /** 子类提供初始状态。 */
    protected abstract fun initialState(): S

    /** 子类处理意图。 */
    protected abstract fun handleIntent(intent: I)

    /** 更新 State（线程安全）。 */
    protected fun updateState(reducer: S.() -> S) {
        _uiState.value = _uiState.value.reducer()
    }

    /** 发送一次性事件。 */
    protected fun sendEffect(effect: E) {
        viewModelScope.launch { _uiEffect.send(effect) }
    }
}
