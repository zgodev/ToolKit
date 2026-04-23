package com.zhangyt.common.router

/**
 * 全局路由表。所有业务模块的页面路径统一在此定义。
 *
 * 路由路径规则：/模块名/页面名
 *
 * 使用示例：
 * ```
 * ARouter.getInstance()
 *     .build(RouterPath.Login.ACTIVITY_LOGIN)
 *     .withString("account", "tom")
 *     .navigation()
 * ```
 */
object RouterPath {

    /* ---------- 登录模块 ---------- */
    object Login {
        const val ACTIVITY_LOGIN = "/login/activity_login"
    }

    /* ---------- 主页模块 ---------- */
    object Home {
        const val ACTIVITY_MAIN = "/home/activity_main"

        const val FRAGMENT_CHAT = "/home/fragment_chat"
        const val FRAGMENT_CONTACTS = "/home/fragment_contacts"
        const val FRAGMENT_DISCOVER = "/home/fragment_discover"
    }

    /* ---------- 我的模块（暂放在 module_home 内） ---------- */
    object Mine {
        const val ACTIVITY_SETTINGS = "/mine/activity_settings"
        const val ACTIVITY_OTA = "/test/activity_ota"

    }

    /* ---------- 通用页面 ---------- */
    object Common {
        const val ACTIVITY_WEB = "/common/activity_web"
    }
}
