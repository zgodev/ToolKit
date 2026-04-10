package com.zhangyt.common.mvi

/**
 * MVI 架构核心契约
 * ---------------------------------------------
 * - [IUiState]  屏幕上的所有可渲染状态（不可变）
 * - [IUiIntent] 用户的意图（按钮点击、下拉刷新等）
 * - [IUiEffect] 一次性事件（弹 Toast、导航跳转、播放动画等）
 *
 * 典型实现：
 * ```
 * data class LoginState(
 *     val loading: Boolean = false,
 *     val error: String? = null,
 *     val user: User? = null
 * ) : IUiState
 *
 * sealed class LoginIntent : IUiIntent {
 *     data class Submit(val name: String, val pwd: String) : LoginIntent()
 * }
 *
 * sealed class LoginEffect : IUiEffect {
 *     object NavigateHome : LoginEffect()
 *     data class Toast(val msg: String) : LoginEffect()
 * }
 * ```
 */
interface IUiState
interface IUiIntent
interface IUiEffect
