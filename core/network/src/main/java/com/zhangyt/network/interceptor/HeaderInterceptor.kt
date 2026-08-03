package com.zhangyt.network.interceptor

import com.zhangyt.network.config.NetworkConfig
import okhttp3.Interceptor
import okhttp3.Response

/**
 * 统一添加 Header：公共 Header + Token。
 */
class HeaderInterceptor : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val original = chain.request()
        val builder = original.newBuilder()

        // 公共 Header
        NetworkConfig.commonHeaders.forEach { (k, v) -> builder.header(k, v) }

        // Token
        NetworkConfig.tokenProvider()?.let { token ->
            if (token.isNotEmpty()) builder.header("Authorization", "Bearer $token")
        }
        return chain.proceed(builder.build())
    }
}
