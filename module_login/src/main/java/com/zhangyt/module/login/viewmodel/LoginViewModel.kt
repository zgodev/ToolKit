package com.zhangyt.module.login.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.zhangyt.common.base.BaseViewModel
import com.zhangyt.common.user.UserInfo
import com.zhangyt.common.user.UserManager
import com.zhangyt.module.login.repository.LoginRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import javax.inject.Inject

/**
 * 登录 ViewModel（MVVM 示例，Hilt 注入 Repository）。
 *
 * -----------------------------------------------------------------
 * Hilt 用法说明：
 *
 *   @HiltViewModel       —— 标记这是一个由 Hilt 管理的 ViewModel。
 *                            底层会生成一个 HiltViewModelFactory，
 *                            让 Activity / Fragment 里的 `by viewModels()`
 *                            自动走 Hilt 注入路径，而不是默认的无参构造。
 *   @Inject constructor  —— 告诉 Hilt "这个 VM 需要一个 LoginRepository"。
 *                            Hilt 顺着依赖链找到 LoginRepository → LoginApi
 *                            → Retrofit → OkHttpClient，全部在编译期连好线。
 *
 * 和传统写法对比：
 *   传统：`private val repository = LoginRepository()`  // 自己 new
 *         每换一次 Repository 实现或想塞 mock，都要改源码。
 *   Hilt：repository 由容器注入；单测只要绑定一个 fake repo 就能替换整条链。
 * -----------------------------------------------------------------
 */
@HiltViewModel
class LoginViewModel @Inject constructor(
    private val repository: LoginRepository
) : BaseViewModel() {

    /** 登录结果：成功后携带 UserInfo；失败时通过 [errorState] 通知。 */
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
