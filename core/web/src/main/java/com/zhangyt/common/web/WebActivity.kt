package com.zhangyt.common.web

import android.view.KeyEvent
import android.view.ViewGroup
import android.widget.FrameLayout
import com.alibaba.android.arouter.facade.annotation.Route
import com.zhangyt.common.base.BaseActivity
import com.zhangyt.common.router.RouterPath
import com.zhangyt.core.web.R
import com.zhangyt.core.web.databinding.ActivityWebBinding

/**
 * WebView 封装，支持原生 WebView 和 TBS X5 双引擎。
 *
 * ## 启动参数（Intent Extras）
 * | Key          | 类型   | 说明                                         |
 * |-------------|--------|----------------------------------------------|
 * | url         | String | 要加载的页面地址                               |
 * | web_engine  | String | 引擎类型：[WebEngine.NATIVE] / [WebEngine.X5] |
 *
 * ## 使用示例
 * ```kotlin
 * // 使用全局默认引擎
 * val intent = Intent(context, WebActivity::class.java).apply {
 *     putExtra(WebActivity.EXTRA_URL, "https://example.com")
 * }
 *
 * // 强制使用 X5 引擎
 * val intent = Intent(context, WebActivity::class.java).apply {
 *     putExtra(WebActivity.EXTRA_URL, "https://example.com")
 *     putExtra(WebActivity.EXTRA_WEB_ENGINE, WebEngine.X5.name)
 * }
 * ```
 *
 * 独立进程：将 WebView 放到独立进程，防止 Crash 影响主进程，也便于内存回收。代价是 IPC 通信成本。
 */
@Route(path = RouterPath.Common.ACTIVITY_WEB)
class WebActivity : BaseActivity<ActivityWebBinding>() {

    private var webViewWrapper: WebViewWrapper? = null
    private lateinit var container: FrameLayout

    override fun initView() {
        super.initView()
        container = findViewById(R.id.web_container)

        // 从 Intent 中读取引擎配置，支持页面级覆盖
        val engineOverride = intent.getStringExtra(EXTRA_WEB_ENGINE)?.let {
            runCatching { WebEngine.valueOf(it) }.getOrNull()
        }

        webViewWrapper = WebViewPool.acquire(this, engineOverride).also { wrapper ->
            container.addView(
                wrapper.view,
                FrameLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )
            )

            // 根据引擎类型设置对应的 WebViewClient
            when (wrapper.engine) {
                WebEngine.NATIVE -> wrapper.setNativeWebViewClient(NativeClient())
                WebEngine.X5 -> wrapper.setX5WebViewClient(X5Client())
            }

            val url = intent.getStringExtra(EXTRA_URL)
            if (url.isNullOrBlank()) {
                finish()
                return
            }
            wrapper.loadUrl(url)
        }
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            webViewWrapper?.let {
                if (it.canGoBack()) {
                    it.goBack()
                    return true
                }
            }
        }
        return super.onKeyDown(keyCode, event)
    }

    override fun onDestroy() {
        // release 内部会 removeFromParent + 清理状态，无需手动 removeView
        webViewWrapper?.let { WebViewPool.release(it) }
        webViewWrapper = null
        super.onDestroy()
    }

    // ─── WebViewClient 实现 ───────────────────────────────────

    /** 原生 WebViewClient */
    private class NativeClient : android.webkit.WebViewClient() {
        // 业务逻辑（URL 拦截、错误处理等）
    }

    /** X5 WebViewClient */
    private class X5Client : com.tencent.smtt.sdk.WebViewClient() {
        // 业务逻辑（URL 拦截、错误处理等）
    }

    companion object {
        const val EXTRA_URL = "url"
        const val EXTRA_WEB_ENGINE = "web_engine"
    }
}
