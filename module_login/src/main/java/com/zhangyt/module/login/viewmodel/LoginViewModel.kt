package com.zhangyt.module.login.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.zhangyt.common.base.BaseViewModel
import com.zhangyt.common.user.UserInfo
import com.zhangyt.common.user.UserManager
import com.zhangyt.module.login.repository.LoginRepository
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart

/**
 * 登录 ViewModel（MVVM 示例）。
 */
class LoginViewModel : BaseViewModel() {

    private val repository = LoginRepository()

    /** 登录结果：成功后携带 UserInfo；失败时通过 [loadState] 通知。 */
    val loginResult = MutableLiveData<UserInfo>()
    val loadingState = MutableLiveData<Boolean>()
    val errorState = MutableLiveData<String>()

    fun login(account: String, password: String) {
        repository.login(account, password)
            .onStart { loadingState.value = true }
            .onEach { user ->
                UserManager.saveUser(user)
                loginResult.value = user
            }
            .catch { errorState.value = it.message ?: "登录失败" }
            .onCompletion { loadingState.value = false }
            .launchIn(viewModelScope)
    }
}
