package com.zhangyt.module.login.repository

import com.zhangyt.core.model.UserInfo
import com.zhangyt.network.repository.BaseRepository
import com.zhangyt.module.login.api.LoginApi
import com.zhangyt.module.login.api.LoginRequest
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 登录 Repository。
 *
 * 为了方便演示，此处用假数据：网络层请求失败时会返回假用户。
 * 实际开发中请直接返回 [api.login] 的结果。
 *
 * -----------------------------------------------------------------
 * Hilt 用法说明：
 *
 *   @Singleton            —— 整个 App 进程共享同一个 LoginRepository 实例
 *                            （不加则每次注入都新建一个）。
 *   @Inject constructor   —— 告诉 Hilt "这个类怎么造"：需要一个 LoginApi。
 *                            Hilt 会顺着依赖链自动从 [LoginModule] 拿到 LoginApi。
 *   private val api       —— 依赖变成构造参数而不是字段硬编码，好处：
 *                              a) 单测可以 new LoginRepository(fakeApi)
 *                              b) 换接口实现不用改本类
 *                              c) 编译期校验，漏了绑定会编译失败而不是运行崩
 *
 * 使用方：LoginViewModel 通过 @Inject 拿到它 ——
 *   `class LoginViewModel @Inject constructor(private val repo: LoginRepository)`
 * -----------------------------------------------------------------
 */
@Singleton
class LoginRepository @Inject constructor(
    private val api: LoginApi
) : BaseRepository() {

    /**
     * 账号密码登录（演示版 - 返回假数据，绕过网络）。
     * 正式接入真实后端时，可替换为：
     * ```
     * fun login(account: String, pwd: String): Flow<UserInfo> = request {
     *     api.login(LoginRequest(account, pwd))
     * }.map { UserInfo(it.userId, it.nickname, it.avatar, it.phone, it.token) }
     * ```
     */
    fun login(account: String, password: String): Flow<UserInfo> = flow {
        // 模拟网络延迟
        kotlinx.coroutines.delay(800)
        if (password.length < 6) {
            throw IllegalArgumentException("密码至少 6 位")
        }
        // 假数据
        val fakeUser = UserInfo(
            userId = "10001",
            nickname = account.ifEmpty { "测试用户" },
            avatar = "https://fake.api.example.com/avatar.jpg",
            phone = account,
            token = "fake_token_${System.currentTimeMillis()}"
        )
        emit(fakeUser)
    }

    fun login2(account: String, pwd: String): Flow<UserInfo> = request {
        api.login(LoginRequest(account, pwd))
    }.map { UserInfo(it.userId, it.nickname, it.avatar, it.phone, it.token) }
}
