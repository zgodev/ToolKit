package com.zhangyt.network.config

import com.zhangyt.network.BuildConfig
import java.io.File

/**
 * 网络层配置。调用方在 Application 中设置，如：
 * ```
 * NetworkConfig.baseUrl   = BuildConfig.BASE_URL
 * NetworkConfig.debuggable = BuildConfig.DEBUG
 * NetworkConfig.tokenProvider = { UserManager.getToken() }
 * NetworkConfig.cacheDir = context.cacheDir.resolve("http")
 * NetworkConfig.onUnauthorized = { UserManager.logout() }
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

    /** 缓存目录（空则不启用 HTTP 缓存）。 */
    var cacheDir: File? = null

    /** 缓存大小（字节）。默认 10MB。 */
    var cacheSize: Long = 10L * 1024 * 1024

    /**
     * 401 未授权回调。OkHttp 响应 `401` 或业务 `code == 401` 时触发一次。
     * 通常用于清 Token + 广播登出事件。
     */
    var onUnauthorized: () -> Unit = {}

    /** 日志截断阈值（字节）。debug 日志 body 超过此值会被截断。默认 4KB。 */
    var logBodyMaxBytes: Long = 4 * 1024
}
