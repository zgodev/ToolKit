package com.zhangyt.toolkit

import com.zhangyt.common.CommonApplication
import com.zhangyt.common.user.SessionEvents
import com.zhangyt.common.user.UserManager
import com.zhangyt.network.config.NetworkConfig
import dagger.hilt.android.HiltAndroidApp
import java.io.File

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
 *
 * -----------------------------------------------------------------
 * [HiltAndroidApp] —— Hilt 的入口注解。
 *   作用：在编译期为本 Application 生成一个根 DI 容器（SingletonComponent），
 *        App 进程启动时自动构建依赖关系图。所有 @AndroidEntryPoint 修饰的
 *        Activity/Fragment/Service 都会顺着这个图找自己需要的依赖。
 *   必须：整个 App 只能有一个 @HiltAndroidApp 的类，且是 Application 的子类。
 * -----------------------------------------------------------------
 */
@HiltAndroidApp
class App : CommonApplication() {

    override fun onCreate() {
        super.onCreate()

        // 网络层配置
        NetworkConfig.baseUrl = BuildConfig.BASE_URL
        NetworkConfig.debuggable = BuildConfig.DEBUG
        NetworkConfig.tokenProvider = { UserManager.getToken() }
        NetworkConfig.cacheDir = File(cacheDir, "http")
        NetworkConfig.onUnauthorized = {
            UserManager.logout()
            SessionEvents.emitLogout()
        }

        // TODO: 其他三方 SDK 初始化，如友盟、Bugly、Crashlytics 等
    }
}
