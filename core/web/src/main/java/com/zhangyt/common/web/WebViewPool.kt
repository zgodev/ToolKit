package com.zhangyt.common.web

import android.app.Activity
import android.app.Application
import android.content.MutableContextWrapper
import java.util.LinkedList

/**
 * WebView 复用池，支持原生 WebView 和 TBS X5 双引擎。
 *
 * 使用方式：
 * ```
 * // Application 中初始化
 * WebViewPool.init(application)
 *
 * // 业务获取
 * val wrapper = WebViewPool.acquire(activity)
 * container.addView(wrapper.view, layoutParams)
 * wrapper.loadUrl("https://example.com")
 *
 * // 业务归还
 * WebViewPool.release(wrapper)
 * ```
 *
 * @author zhangyt
 * @date 2026/4/28
 */
object WebViewPool {

    private const val MAX_POOL_SIZE = 2

    /** 原生 WebView 池 */
    private val nativePool: LinkedList<WebViewWrapper> = LinkedList()
    /** X5 WebView 池 */
    private val x5Pool: LinkedList<WebViewWrapper> = LinkedList()

    private lateinit var app: Application

    /**
     * 在 Application 中初始化（仅保存引用）。
     *
     * **注意**：不在此处 prepareWebView，因为如果全局配置了 X5，
     * 此时 TBS 可能还未初始化完成，过早预热会全部创建原生 WebView。
     * 预热时机由 [onEngineReady] 控制。
     */
    fun init(application: Application) {
        app = application
    }

    /**
     * 引擎就绪后调用，预热指定引擎的 WebView。
     *
     * - 原生引擎：可在 [init] 之后立即调用
     * - X5 引擎：应在 TBS `onViewInitFinished(true)` 回调后调用
     */
    fun onEngineReady(engine: WebEngine) {
        prepareWebView(engine)
    }

    /** 预创建 WebView 并放入池中 */
    @Synchronized
    fun prepareWebView(engine: WebEngine = WebEngineConfig.resolveEngine()) {
        val pool = getPool(engine)
        if (pool.size >= MAX_POOL_SIZE) return

        val wrapper = MutableContextWrapper(app)
        val webViewWrapper = WebViewWrapper.create(wrapper, engine).also {
            configWebView(it)
        }
        pool.offer(webViewWrapper)
    }

    /**
     * 从池中获取 WebView。
     *
     * @param activity 当前 Activity，用于切换 Context
     * @param engine 指定引擎类型，为 null 时使用全局配置
     */
    @Synchronized
    fun acquire(activity: Activity, engine: WebEngine? = null): WebViewWrapper {
        val resolvedEngine = WebEngineConfig.resolveEngine(engine)
        val pool = getPool(resolvedEngine)

        val wrapper = pool.poll() ?: run {
            // 池空了，临时创建一个
            val ctxWrapper = MutableContextWrapper(activity)
            WebViewWrapper.create(ctxWrapper, resolvedEngine).also { configWebView(it) }
        }
        // 切换 Context 到当前 Activity
        (wrapper.context as? MutableContextWrapper)?.baseContext = activity
        // 异步预热下一个，保证下次也能立即拿到
        wrapper.post { prepareWebView(resolvedEngine) }
        return wrapper
    }

    /**
     * 业务用完归还 WebView。
     * 会自动清理状态、切换 Context 回 Application。
     */
    @Synchronized
    fun release(wrapper: WebViewWrapper?) {
        wrapper ?: return
        val pool = getPool(wrapper.engine)
        if (pool.size >= MAX_POOL_SIZE) {
            destroyWebView(wrapper)
            return
        }
        resetWebView(wrapper)
        (wrapper.context as? MutableContextWrapper)?.baseContext = app
        pool.offer(wrapper)
    }

    /** 清空所有池（低内存时可调用） */
    @Synchronized
    fun clearAll() {
        nativePool.forEach { destroyWebView(it) }
        nativePool.clear()
        x5Pool.forEach { destroyWebView(it) }
        x5Pool.clear()
    }

    // ─── 内部方法 ─────────────────────────────────────────────

    private fun getPool(engine: WebEngine): LinkedList<WebViewWrapper> {
        return when (engine) {
            WebEngine.NATIVE -> nativePool
            WebEngine.X5 -> x5Pool
        }
    }

    private fun resetWebView(wrapper: WebViewWrapper) {
        wrapper.stopLoading()
        wrapper.loadUrl("about:blank")
        wrapper.clearHistory()
        wrapper.clearFormData()
        wrapper.clearMatches()
        wrapper.clearSslPreferences()
        wrapper.resetWebChromeClient()
        wrapper.resetWebViewClient()
        wrapper.resetDownloadListener()
        // 解绑业务注册的 JSBridge
        wrapper.removeJavascriptInterface("xxx") // TODO: 移除自定义 JSBridge
        wrapper.removeFromParent()
    }

    private fun destroyWebView(wrapper: WebViewWrapper) {
        resetWebView(wrapper)
        wrapper.destroy()
    }

    private fun configWebView(wrapper: WebViewWrapper) {
        wrapper.configureSettings {
            javaScriptEnabled = true
            domStorageEnabled = true
            cacheMode = android.webkit.WebSettings.LOAD_DEFAULT
            useWideViewPort = true
            loadWithOverviewMode = true
            // 允许混合内容（http + https）
            mixedContentMode = android.webkit.WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
        }
    }
}
