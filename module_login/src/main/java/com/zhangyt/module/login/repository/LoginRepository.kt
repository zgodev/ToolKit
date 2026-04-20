package com.zhangyt.module.login.repository

import com.zhangyt.common.base.BaseRepository
import com.zhangyt.common.user.UserInfo
import com.zhangyt.module.login.api.LoginApi
import com.zhangyt.module.login.api.LoginRequest
import com.zhangyt.network.client.RetrofitClient
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map

/**
 * 登录 Repository。
 *
 * 为了方便演示，此处用假数据：网络层请求失败时会返回假用户。
 * 实际开发中请直接返回 [api.login] 的结果。
 */
class LoginRepository : BaseRepository() {

    private val api by lazy { RetrofitClient.create(LoginApi::class.java) }

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
