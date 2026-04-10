package com.zhangyt.common.router

import android.content.Context
import android.os.Bundle
import com.alibaba.android.arouter.facade.Postcard
import com.alibaba.android.arouter.launcher.ARouter

/**
 * ARouter 二次封装，方便跳转。
 *
 * 示例：
 * ```
 * RouterManager.start(RouterPath.Login.ACTIVITY_LOGIN)
 * RouterManager.start(RouterPath.Common.ACTIVITY_WEB) {
 *     withString("url", "https://www.baidu.com")
 * }
 * ```
 */
object RouterManager {

    /** 直接跳转。 */
    fun start(path: String, bundle: Bundle? = null) {
        ARouter.getInstance().build(path).apply {
            if (bundle != null) with(bundle)
        }.navigation()
    }

    /** 通过 DSL 自定义参数跳转。 */
    fun start(path: String, block: Postcard.() -> Unit) {
        ARouter.getInstance().build(path).apply(block).navigation()
    }

    /** 返回 Fragment 实例（用于主页 Tab 动态加载）。 */
    fun getFragment(path: String): androidx.fragment.app.Fragment? {
        return ARouter.getInstance().build(path).navigation() as? androidx.fragment.app.Fragment
    }
}
