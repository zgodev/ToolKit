package com.zhangyt.common.web

import android.annotation.SuppressLint
import android.content.Context
import android.content.MutableContextWrapper
import android.view.View
import android.view.ViewGroup

/**
 * WebView 统一封装。
 *
 * 由于原生 [android.webkit.WebView] 和 X5 [com.tencent.smtt.sdk.WebView]
 * 不共享类层级，此 Wrapper 提供统一的 API 代理，业务层无需关心底层引擎差异。
 *
 * 通过 [WebViewWrapper.create] 工厂方法创建。
 */
class WebViewWrapper private constructor(
    private val nativeWebView: android.webkit.WebView?,
    private val x5WebView: com.tencent.smtt.sdk.WebView?,
) {
    /** 当前使用的引擎类型 */
    val engine: WebEngine = if (x5WebView != null) WebEngine.X5 else WebEngine.NATIVE

    /** 获取底层 View，用于 addView / removeView */
    val view: View get() = nativeWebView ?: x5WebView!!

    /** 获取 Context（MutableContextWrapper） */
    val context: Context get() = view.context

    // ─── 内部分发 ────────────────────────────────────────────

    /**
     * 统一分发：根据当前引擎调用对应 WebView 的方法。
     * 避免 `nativeWebView?.xxx() ?: x5WebView?.xxx()` 的 `?:` 语义陷阱
     * （Kotlin 中 Unit 非 null，`?:` 右侧永远不执行——虽然当前构造互斥不会出错，
     *  但语义不清晰，后续维护容易踩坑）。
     */
    private inline fun <R> dispatch(
        nativeAction: (android.webkit.WebView) -> R,
        x5Action: (com.tencent.smtt.sdk.WebView) -> R,
    ): R {
        return if (nativeWebView != null) nativeAction(nativeWebView) else x5Action(x5WebView!!)
    }

    // ─── 通用操作代理 ────────────────────────────────────────

    fun loadUrl(url: String) = dispatch({ it.loadUrl(url) }, { it.loadUrl(url) })

    fun stopLoading() = dispatch({ it.stopLoading() }, { it.stopLoading() })

    fun clearHistory() = dispatch({ it.clearHistory() }, { it.clearHistory() })

    fun clearFormData() = dispatch({ it.clearFormData() }, { it.clearFormData() })

    fun clearMatches() = dispatch({ it.clearMatches() }, { it.clearMatches() })

    fun clearSslPreferences() = dispatch({ it.clearSslPreferences() }, { it.clearSslPreferences() })

    fun destroy() = dispatch({ it.destroy() }, { it.destroy() })

    fun removeJavascriptInterface(name: String) = dispatch(
        { it.removeJavascriptInterface(name) },
        { it.removeJavascriptInterface(name) },
    )

    @SuppressLint("JavascriptInterface")
    fun addJavascriptInterface(obj: Any, name: String) = dispatch(
        { it.addJavascriptInterface(obj, name) },
        { it.addJavascriptInterface(obj, name) },
    )

    fun canGoBack(): Boolean = dispatch({ it.canGoBack() }, { it.canGoBack() })

    fun goBack() = dispatch({ it.goBack() }, { it.goBack() })

    fun post(action: Runnable): Boolean = view.post(action)

    // ─── WebViewClient ────────────────────────────────────────

    fun setNativeWebViewClient(client: android.webkit.WebViewClient) {
        nativeWebView?.webViewClient = client
    }

    fun setX5WebViewClient(client: com.tencent.smtt.sdk.WebViewClient) {
        x5WebView?.webViewClient = client
    }

    /** 重置 WebViewClient 为默认空实现 */
    fun resetWebViewClient() {
        nativeWebView?.webViewClient = android.webkit.WebViewClient()
        x5WebView?.webViewClient = com.tencent.smtt.sdk.WebViewClient()
    }

    // ─── WebChromeClient ──────────────────────────────────────

    fun setNativeWebChromeClient(client: android.webkit.WebChromeClient?) {
        nativeWebView?.webChromeClient = client
    }

    fun setX5WebChromeClient(client: com.tencent.smtt.sdk.WebChromeClient?) {
        x5WebView?.webChromeClient = client
    }

    fun resetWebChromeClient() {
        nativeWebView?.webChromeClient = null
        x5WebView?.webChromeClient = null
    }

    // ─── DownloadListener ─────────────────────────────────────

    fun setNativeDownloadListener(listener: android.webkit.DownloadListener?) {
        nativeWebView?.setDownloadListener(listener)
    }

    fun setX5DownloadListener(listener: com.tencent.smtt.sdk.DownloadListener?) {
        x5WebView?.setDownloadListener(listener)
    }

    fun resetDownloadListener() {
        nativeWebView?.setDownloadListener(null)
        x5WebView?.setDownloadListener(null)
    }

    // ─── Settings ─────────────────────────────────────────────

    /** 统一配置 WebSettings */
    fun configureSettings(block: WebSettingsProxy.() -> Unit) {
        val proxy = if (nativeWebView != null) {
            WebSettingsProxy.Native(nativeWebView.settings)
        } else {
            WebSettingsProxy.X5(x5WebView!!.settings)
        }
        proxy.block()
    }

    // ─── 从父布局移除 ─────────────────────────────────────────

    fun removeFromParent() {
        (view.parent as? ViewGroup)?.removeView(view)
    }

    // ─── 工厂方法 ─────────────────────────────────────────────

    companion object {

        /**
         * 创建 WebViewWrapper。
         *
         * @param context 建议传入 [MutableContextWrapper]，方便池复用时切换 Context
         * @param engine 指定引擎，默认根据全局配置自动选择
         */
        fun create(
            context: Context,
            engine: WebEngine = WebEngineConfig.resolveEngine(),
        ): WebViewWrapper {
            return when (engine) {
                WebEngine.X5 -> {
                    val webView = com.tencent.smtt.sdk.WebView(context)
                    WebViewWrapper(nativeWebView = null, x5WebView = webView)
                }
                WebEngine.NATIVE -> {
                    val webView = android.webkit.WebView(context)
                    WebViewWrapper(nativeWebView = webView, x5WebView = null)
                }
            }
        }
    }
}

/**
 * WebSettings 代理，统一原生和 X5 的 Settings API
 */
sealed class WebSettingsProxy {

    abstract var javaScriptEnabled: Boolean
    abstract var domStorageEnabled: Boolean
    abstract var cacheMode: Int
    abstract var useWideViewPort: Boolean
    abstract var loadWithOverviewMode: Boolean
    abstract var allowFileAccess: Boolean
    abstract var javaScriptCanOpenWindowsAutomatically: Boolean
    abstract var mixedContentMode: Int

    class Native(private val settings: android.webkit.WebSettings) : WebSettingsProxy() {
        override var javaScriptEnabled
            get() = settings.javaScriptEnabled
            set(value) { settings.javaScriptEnabled = value }
        override var domStorageEnabled
            get() = settings.domStorageEnabled
            set(value) { settings.domStorageEnabled = value }
        override var cacheMode
            get() = settings.cacheMode
            set(value) { settings.cacheMode = value }
        override var useWideViewPort
            get() = settings.useWideViewPort
            set(value) { settings.useWideViewPort = value }
        override var loadWithOverviewMode
            get() = settings.loadWithOverviewMode
            set(value) { settings.loadWithOverviewMode = value }
        override var allowFileAccess
            get() = settings.allowFileAccess
            set(value) { settings.allowFileAccess = value }
        override var javaScriptCanOpenWindowsAutomatically
            get() = settings.javaScriptCanOpenWindowsAutomatically
            set(value) { settings.javaScriptCanOpenWindowsAutomatically = value }
        override var mixedContentMode
            get() = settings.mixedContentMode
            set(value) { settings.mixedContentMode = value }
    }

    class X5(private val settings: com.tencent.smtt.sdk.WebSettings) : WebSettingsProxy() {
        override var javaScriptEnabled
            get() = settings.javaScriptEnabled
            set(value) { settings.javaScriptEnabled = value }
        override var domStorageEnabled
            get() = settings.domStorageEnabled
            set(value) { settings.domStorageEnabled = value }
        override var cacheMode
            get() = settings.cacheMode
            set(value) { settings.cacheMode = value }
        override var useWideViewPort
            get() = settings.useWideViewPort
            set(value) { settings.useWideViewPort = value }
        override var loadWithOverviewMode
            get() = settings.loadWithOverviewMode
            set(value) { settings.loadWithOverviewMode = value }
        override var allowFileAccess
            get() = settings.allowFileAccess
            set(value) { settings.allowFileAccess = value }
        override var javaScriptCanOpenWindowsAutomatically
            get() = settings.javaScriptCanOpenWindowsAutomatically
            set(value) { settings.javaScriptCanOpenWindowsAutomatically = value }
        override var mixedContentMode
            get() = settings.mixedContentMode
            set(value) { settings.mixedContentMode = value }
    }
}
