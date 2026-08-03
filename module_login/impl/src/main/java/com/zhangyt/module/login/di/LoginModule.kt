package com.zhangyt.module.login.di

import com.zhangyt.module.login.api.LoginApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

/**
 * 登录模块的 Hilt Module —— 把 [LoginApi] "生产方式"告诉 Hilt。
 *
 * 为什么需要它：
 * - [LoginApi] 是 Retrofit 接口，**没有构造函数可 @Inject**；
 *   必须用 `retrofit.create(LoginApi::class.java)` 来生成动态代理。
 * - 每个业务模块的 Api 都应在自己模块里用这种方式"登记"一次，
 *   这样 Hilt 在需要 `LoginApi` 时才能找到造法。
 *
 * 依赖链示意：
 *   `LoginRepository(@Inject api: LoginApi)`
 *      ↑ Hilt 去哪里找 LoginApi？ ——
 *   `LoginModule.provideLoginApi(retrofit)`
 *      ↑ retrofit 从哪来？ ——
 *   `NetworkModule.provideRetrofit()`
 */
@Module
@InstallIn(SingletonComponent::class)
object LoginModule {

    @Provides
    @Singleton
    fun provideLoginApi(retrofit: Retrofit): LoginApi = retrofit.create(LoginApi::class.java)
}
