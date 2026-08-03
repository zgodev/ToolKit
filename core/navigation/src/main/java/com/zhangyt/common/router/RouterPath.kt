package com.zhangyt.common.router

/**
 * core 层通用路由表。业务路由由各 feature 的 api 模块定义。
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
}
