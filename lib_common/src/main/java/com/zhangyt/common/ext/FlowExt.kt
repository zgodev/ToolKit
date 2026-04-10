package com.zhangyt.common.ext

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart

/**
 * 将 Flow 绑定到生命周期，提供 DSL 方式处理回调。
 *
 * 示例：
 * ```
 * repository.login(name, pwd).collectIn(this) {
 *     onStart    { showLoading() }
 *     onSuccess  { toast("登录成功") }
 *     onError    { toast(it.message ?: "网络错误") }
 *     onComplete { hideLoading() }
 * }
 * ```
 *
 * 实现使用 `onEach + launchIn`，避免直接调用 `collect { }` 带出的
 * `@InternalCoroutinesApi` 警告。
 */
fun <T> Flow<T>.collectIn(owner: LifecycleOwner, builder: FlowCallback<T>.() -> Unit) {
    val callback = FlowCallback<T>().apply(builder)
    this
        .onStart { callback.startBlock?.invoke() }
        .onEach { callback.successBlock?.invoke(it) }
        .catch { callback.errorBlock?.invoke(it) }
        .onCompletion { callback.completeBlock?.invoke() }
        .launchIn(owner.lifecycleScope)
}

class FlowCallback<T> {
    internal var startBlock: (() -> Unit)? = null
    internal var successBlock: ((T) -> Unit)? = null
    internal var errorBlock: ((Throwable) -> Unit)? = null
    internal var completeBlock: (() -> Unit)? = null

    fun onStart(block: () -> Unit) { startBlock = block }
    fun onSuccess(block: (T) -> Unit) { successBlock = block }
    fun onError(block: (Throwable) -> Unit) { errorBlock = block }
    fun onComplete(block: () -> Unit) { completeBlock = block }
}
