package com.zhangyt.common.web

/**
 * WebView 引擎类型
 */
enum class WebEngine {
    /** 系统原生 WebView */
    NATIVE,
    /** 腾讯 TBS X5 内核 */
    X5
}

/**
 * WebView 全局配置
 *
 * 在 Application 初始化时设置引擎类型：
 * ```
 * WebEngineConfig.engine = WebEngine.X5
 * ```
 *
 * 也可以在 WebActivity 启动 Intent 中通过 extra 覆盖：
 * ```
 * intent.putExtra("web_engine", WebEngine.NATIVE.name)
 * ```
 */
object WebEngineConfig {

    /** 全局默认引擎，默认使用原生 WebView */
    @Volatile
    var engine: WebEngine = WebEngine.NATIVE

    /** TBS X5 内核是否初始化成功 */
    @Volatile
    var isX5Initialized: Boolean = false
        internal set

    /**
     * 获取实际应该使用的引擎。
     * 如果配置了 X5 但初始化失败，会降级到 NATIVE。
     */
    fun resolveEngine(override: WebEngine? = null): WebEngine {
        val target = override ?: engine
        return if (target == WebEngine.X5 && !isX5Initialized) {
            // X5 未初始化成功，降级到原生
            WebEngine.NATIVE
        } else {
            target
        }
    }
}
