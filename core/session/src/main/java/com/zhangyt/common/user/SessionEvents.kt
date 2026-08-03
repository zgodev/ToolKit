package com.zhangyt.common.user

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

/**
 * 全局会话事件总线（登出 / Token 失效）。
 *
 * 发出方：`NetworkConfig.onUnauthorized` 回调或业务主动调用 [emitLogout]。
 * 订阅方：业务模块的 MainActivity 订阅 [logoutFlow] 跳登录页。
 *
 * 示例：
 * ```
 * lifecycleScope.launch {
 *     repeatOnLifecycle(Lifecycle.State.STARTED) {
 *         SessionEvents.logoutFlow.collect { /* 跳登录页 */ }
 *     }
 * }
 * ```
 */
object SessionEvents {

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)

    // replay = 0：只通知在线订阅者；extraBufferCapacity 防止 tryEmit 被阻塞
    private val _logout = MutableSharedFlow<Unit>(replay = 0, extraBufferCapacity = 1)
    val logoutFlow: SharedFlow<Unit> = _logout.asSharedFlow()

    /** 非阻塞发射；在后台线程也安全。 */
    fun emitLogout() {
        scope.launch { _logout.emit(Unit) }
    }
}
