package com.zhangyt.network.client

import com.zhangyt.network.config.NetworkConfig
import com.zhangyt.network.interceptor.HeaderInterceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

/**
 * Retrofit 单例客户端。
 *
 * 典型使用：
 * ```
 * object ApiFactory {
 *     val loginApi: LoginApi = RetrofitClient.create(LoginApi::class.java)
 * }
 * ```
 */
object RetrofitClient {

    private val retrofit: Retrofit by lazy { buildRetrofit() }

    /** 动态创建任意 ApiService。 */
    fun <T> create(api: Class<T>): T = retrofit.create(api)

    private fun buildRetrofit(): Retrofit {
        val client = OkHttpClient.Builder()
            .connectTimeout(NetworkConfig.connectTimeout, TimeUnit.SECONDS)
            .readTimeout(NetworkConfig.readTimeout, TimeUnit.SECONDS)
            .writeTimeout(NetworkConfig.writeTimeout, TimeUnit.SECONDS)
            .addInterceptor(HeaderInterceptor())
            .apply {
                if (NetworkConfig.debuggable) {
                    addInterceptor(HttpLoggingInterceptor().apply {
                        level = HttpLoggingInterceptor.Level.BODY
                    })
                }
            }
            .retryOnConnectionFailure(true)
            .build()

        return Retrofit.Builder()
            .baseUrl(NetworkConfig.baseUrl)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
}
