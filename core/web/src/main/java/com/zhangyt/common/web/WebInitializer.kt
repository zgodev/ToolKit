package com.zhangyt.common.web

import android.app.Application
import android.os.Handler
import android.os.Looper
import com.tencent.smtt.export.external.TbsCoreSettings
import com.tencent.smtt.sdk.QbSdk
import com.zhangyt.common.utils.AppStartup

/**
 * Web 能力的显式初始化入口。
 *
 * core:startup 不依赖 core:web；只有真正集成 Web 能力的应用壳才调用这里，
 * 从而避免基础 Application 隐式拉入 TBS 和 WebView 预热开销。
 */
object WebInitializer {

    fun initialize(application: Application) {
        WebViewPool.init(application)
        Handler(Looper.getMainLooper()).post {
            WebViewPool.onEngineReady(WebEngine.NATIVE)
        }
        AppStartup.asyncInit("tbs") { initializeTbs(application) }
    }

    private fun initializeTbs(application: Application) {
        QbSdk.initTbsSettings(
            mapOf(
                TbsCoreSettings.TBS_SETTINGS_USE_SPEEDY_CLASSLOADER to true,
                TbsCoreSettings.TBS_SETTINGS_USE_DEXLOADER_SERVICE to true,
            )
        )
        QbSdk.initX5Environment(application, object : QbSdk.PreInitCallback {
            override fun onCoreInitFinished() = Unit

            override fun onViewInitFinished(isX5Core: Boolean) {
                WebEngineConfig.isX5Initialized = isX5Core
                if (isX5Core) {
                    Handler(Looper.getMainLooper()).post {
                        WebViewPool.onEngineReady(WebEngine.X5)
                    }
                }
            }
        })
    }
}
