package com.zhangyt.network.config

import com.zhangyt.network.BuildConfig

/**
 * 网络层配置。调用方在 Application 中设置，如：
 * ```
 * NetworkConfig.baseUrl   = BuildConfig.BASE_URL
 * NetworkConfig.debuggable = BuildConfig.DEBUG
 * NetworkConfig.tokenProvider = { UserManager.getToken() }
 * ```
 */
object NetworkConfig {
    /** 根 URL，建议末尾带 `/`。可替换为 BuildConfig.BASE_URL 以支持多环境。 */
    var baseUrl: String = "https://fake.api.example.com/"

    /** 是否打印日志。release 关闭。 */
    var debuggable: Boolean = BuildConfig.DEBUG

    /** 连接超时（秒） */
    var connectTimeout: Long = 15

    /** 读超时（秒） */
    var readTimeout: Long = 15

    /** 写超时（秒） */
    var writeTimeout: Long = 15

    /** Token 获取回调。放在 Header 中。 */
    var tokenProvider: () -> String? = { null }

    /** 公共 Header。 */
    var commonHeaders: Map<String, String> = mapOf(
        "Content-Type" to "application/json;charset=UTF-8",
        "Accept" to "application/json"
    )
}
