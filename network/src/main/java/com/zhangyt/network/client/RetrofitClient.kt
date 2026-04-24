package com.zhangyt.network.client

import com.zhangyt.network.config.NetworkConfig
import com.zhangyt.network.interceptor.BodyTruncatingLogger
import com.zhangyt.network.interceptor.HeaderInterceptor
import com.zhangyt.network.interceptor.TokenInterceptor
import okhttp3.Cache
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

/**
 * Retrofit 单例客户端。兼容老调用者：`RetrofitClient.create(...)`；
 * 同时把 [okHttpClient] / [retrofit] 暴露给 Hilt / WebSocket 复用同一连接池。
 */
object RetrofitClient {

    /** 对外暴露的共享 OkHttpClient（连接池复用）。 */
    val okHttpClient: OkHttpClient by lazy { buildOkHttpClient() }

    /** 对外暴露的共享 Retrofit。 */
    val retrofit: Retrofit by lazy { buildRetrofit(okHttpClient) }

    /** 动态创建任意 ApiService。 */
    fun <T> create(api: Class<T>): T = retrofit.create(api)

    private fun buildOkHttpClient(): OkHttpClient {
        val builder = OkHttpClient.Builder()
            .connectTimeout(NetworkConfig.connectTimeout, TimeUnit.SECONDS)
            .readTimeout(NetworkConfig.readTimeout, TimeUnit.SECONDS)
            .writeTimeout(NetworkConfig.writeTimeout, TimeUnit.SECONDS)
            .retryOnConnectionFailure(true)

        // 拦截器链顺序：Header(添加token) → Token(401回调) → Cache(可选) → Logging(debug only)
        builder.addInterceptor(HeaderInterceptor())
        builder.addInterceptor(TokenInterceptor())

        if (NetworkConfig.debuggable) {
            builder.addInterceptor(
                HttpLoggingInterceptor(BodyTruncatingLogger()).apply {
                    level = HttpLoggingInterceptor.Level.BODY
                }
            )
        }

        NetworkConfig.cacheDir?.let { dir ->
            builder.cache(Cache(dir, NetworkConfig.cacheSize))
        }

        return builder.build()
    }

    private fun buildRetrofit(client: OkHttpClient): Retrofit =
        Retrofit.Builder()
            .baseUrl(NetworkConfig.baseUrl)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
}
