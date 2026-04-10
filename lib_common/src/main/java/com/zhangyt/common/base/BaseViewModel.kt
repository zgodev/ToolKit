package com.zhangyt.common.base

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zhangyt.common.state.UiLoadState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

/**
 * ViewModel 基类（MVVM）。
 *
 * - [loadState]  加载态，用于控制 Loading / 成功 / 失败 UI
 * - [launch]     简化协程 + 统一异常处理
 *
 * 示例：
 * ```
 * class LoginViewModel : BaseViewModel() {
 *     val userLiveData = MutableLiveData<User>()
 *     fun login(name: String, pwd: String) = launch(showLoading = true) {
 *         val user = repo.login(name, pwd)
 *         userLiveData.postValue(user)
 *     }
 * }
 * ```
 */
open class BaseViewModel : ViewModel() {

    /** 统一 UI 状态，Activity / Fragment 通过 observe 显示 Loading、错误等 */
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
        } catch (t: Throwable) {
            loadState.postValue(UiLoadState.Error(t.message ?: "未知错误", t))
            onError?.invoke(t)
        }
    }
}
