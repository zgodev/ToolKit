package com.zhangyt.common.router

/**
 * 全局路由契约。按公共能力和业务域分组，避免业务模块互相依赖实现。
 *
 * 路由路径规则：/模块名/页面名
 *
 * 使用示例：
 * ```
 * ARouter.getInstance()
 *     .build(RouterPath.Common.ACTIVITY_WEB)
 *     .withString("account", "tom")
 *     .navigation()
 * ```
 */
object RouterPath {

    object Common {
        const val ACTIVITY_WEB = "/common/activity_web"
    }

    object Login {
        const val ACTIVITY_LOGIN = "/login/activity_login"
    }

    object Home {
        const val ACTIVITY_MAIN = "/main/activity_main"
        const val FRAGMENT_CHAT = "/home/fragment_chat"
        const val FRAGMENT_CONTACTS = "/home/fragment_contacts"
        const val FRAGMENT_DISCOVER = "/home/fragment_discover"
    }

    object Mine {
        const val FRAGMENT_MINE = "/mine/fragment_mine"
    }

    object Ota {
        const val ACTIVITY_OTA = "/ota/activity_ota"
    }
}
