package com.zhangyt.network.interceptor

import com.zhangyt.network.config.NetworkConfig
import okhttp3.Interceptor
import okhttp3.Response
import java.util.concurrent.atomic.AtomicBoolean

/**
 * 检查 401 响应并回调 [NetworkConfig.onUnauthorized]。
 * - 使用 [AtomicBoolean] 限制一段时间内只通知一次，避免并发请求触发多次登出。
 */
class TokenInterceptor : Interceptor {

    private val dispatched = AtomicBoolean(false)

    override fun intercept(chain: Interceptor.Chain): Response {
        val response = chain.proceed(chain.request())
        if (response.code == 401 && dispatched.compareAndSet(false, true)) {
            try {
                NetworkConfig.onUnauthorized()
            } finally {
                // 2 秒后允许再次触发；防止真正的 token 续期流程被长期静默
                Thread {
                    Thread.sleep(2000)
                    dispatched.set(false)
                }.start()
            }
        }
        return response
    }
}
