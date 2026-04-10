package com.zhangyt.common.state

/**
 * 通用 UI 加载状态。
 * - Idle    初始态
 * - Loading 进行中
 * - Success 成功
 * - Error   失败，携带信息
 */
sealed class UiLoadState {
    object Idle : UiLoadState()
    object Loading : UiLoadState()
    object Success : UiLoadState()
    data class Error(val message: String, val throwable: Throwable? = null) : UiLoadState()
}
