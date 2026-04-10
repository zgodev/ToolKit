package com.zhangyt.toolkit

import com.zhangyt.common.CommonApplication
import com.zhangyt.common.user.UserManager
import com.zhangyt.network.config.NetworkConfig

/**
 * 应用入口 Application。
 *
 * 继承 [CommonApplication]，自动完成：
 * - AppManager Activity 栈注册
 * - ARouter 初始化
 * - MMKV 初始化
 * - 主题 / 多语言恢复
 *
 * 这里只放项目特有的初始化（网络层 BaseUrl、埋点、Crash 上报等）。
 */
class App : CommonApplication() {

    override fun onCreate() {
        super.onCreate()

        // 网络层配置
        NetworkConfig.baseUrl = BuildConfig.BASE_URL
        NetworkConfig.debuggable = BuildConfig.DEBUG
        NetworkConfig.tokenProvider = { UserManager.getToken() }

        // TODO: 其他三方 SDK 初始化，如友盟、Bugly、Crashlytics 等
    }
}
