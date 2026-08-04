package com.zhangyt.module.login.repository

import com.zhangyt.module.login.api.LoginApi
import com.zhangyt.module.login.api.LoginRequest
import com.zhangyt.module.login.api.LoginResponse
import com.zhangyt.module.login.api.SmsRequest
import com.zhangyt.network.api.BaseResponse
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Test

/**
 * @description: 验证演示登录仓库的输入约束与用户数据映射
 * @author: zhangyt
 * @Date: 2026-08-04
 */
class LoginRepositoryTest {

    private val unusedApi = object : LoginApi {
        override suspend fun login(body: LoginRequest): BaseResponse<LoginResponse> =
            error("演示登录不应访问网络")

        override suspend fun sendSms(body: SmsRequest): BaseResponse<Unit> =
            error("演示登录不应访问网络")
    }

    @Test
    fun `password shorter than six characters is rejected`() {
        val repository = LoginRepository(unusedApi)

        val error = assertThrows(IllegalArgumentException::class.java) {
            runTest { repository.login("13800000000", "12345").first() }
        }

        assertEquals("密码至少 6 位", error.message)
    }

    @Test
    fun `valid credentials produce the expected demo user`() = runTest {
        val repository = LoginRepository(unusedApi)

        val user = repository.login("13800000000", "123456").first()

        assertEquals("10001", user.userId)
        assertEquals("13800000000", user.nickname)
        assertEquals("13800000000", user.phone)
    }
}
