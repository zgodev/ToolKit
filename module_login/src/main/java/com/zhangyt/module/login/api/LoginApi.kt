package com.zhangyt.module.login.api

import com.zhangyt.network.api.BaseResponse
import retrofit2.http.Body
import retrofit2.http.POST

/** 登录业务 API 接口。对应假的 URL。 */
interface LoginApi {

    /** 账号密码登录。 */
    @POST("api/v1/user/login")
    suspend fun login(@Body body: LoginRequest): BaseResponse<LoginResponse>

    /** 发送短信验证码。 */
    @POST("api/v1/user/sendSms")
    suspend fun sendSms(@Body body: SmsRequest): BaseResponse<Unit>
}

data class LoginRequest(val account: String, val password: String)
data class SmsRequest(val phone: String)

data class LoginResponse(
    val userId: String,
    val nickname: String,
    val avatar: String,
    val phone: String,
    val token: String
)
