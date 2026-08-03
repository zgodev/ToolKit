package com.zhangyt.network.di

import com.google.gson.Gson
import com.zhangyt.network.client.RetrofitClient
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import javax.inject.Singleton

/**
 * 网络层 Hilt 模块 —— 告诉 Hilt "OkHttpClient / Retrofit / Gson 怎么造"。
 *
 * 为什么需要 Hilt Module：
 * - [OkHttpClient]、[Retrofit]、[Gson] 是第三方库的类，我们无法给它们加
 *   `@Inject constructor(...)`；必须用 @Module + @Provides 的方式告诉 Hilt
 *   "这三个类型的实例请按以下代码构造"。
 * - 凡是业务代码想通过 `@Inject` 拿 `OkHttpClient/Retrofit/Gson`，Hilt 都会
 *   回到这里找对应的 @Provides 方法。
 *
 * 为什么复用 [RetrofitClient]：
 * - 项目历史上已有 `RetrofitClient.create(XxxApi::class.java)` 这种老写法在用，
 *   两条路径（Hilt + 单例静态方法）必须指向同一个 OkHttpClient 实例，否则会出现
 *   两份连接池 / 两份拦截器链，浪费且 Token 刷新行为不一致。
 *
 * @Module                 声明这是一个依赖提供模块
 * @InstallIn(Singleton..)  把这些 @Provides 安装到"App 级单例"容器
 * @Provides               标注"这个函数负责生产目标类型的实例"
 * @Singleton              同一个 Component 内只造一次（这里即 App 进程内全局单例）
 */
@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    /** 业务代码 `@Inject lateinit var ok: OkHttpClient` 就会拿到这里返回的同一个实例。 */
    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient = RetrofitClient.okHttpClient

    /** 业务代码 `@Inject lateinit var retrofit: Retrofit` 走这里。 */
    @Provides
    @Singleton
    fun provideRetrofit(): Retrofit = RetrofitClient.retrofit

    /** 任意处需要 Gson 时通过 @Inject 拿到同一个实例。 */
    @Provides
    @Singleton
    fun provideGson(): Gson = Gson()
}
