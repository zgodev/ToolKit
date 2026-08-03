package com.zhangyt.toolkit

import com.zhangyt.common.router.RouterPath
import com.zhangyt.module.home.api.HomeRoutes
import com.zhangyt.module.login.api.LoginRoutes
import com.zhangyt.module.mine.api.MineRoutes
import com.zhangyt.module.ota.api.OtaRoutes
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class RouteContractTest {

    private val routes = listOf(
        LoginRoutes.ACTIVITY_LOGIN,
        HomeRoutes.ACTIVITY_MAIN,
        HomeRoutes.FRAGMENT_CHAT,
        HomeRoutes.FRAGMENT_CONTACTS,
        HomeRoutes.FRAGMENT_DISCOVER,
        MineRoutes.FRAGMENT_MINE,
        OtaRoutes.ACTIVITY_OTA,
        RouterPath.Common.ACTIVITY_WEB,
    )

    @Test
    fun `all routes are absolute and globally unique`() {
        assertTrue(routes.all { it.startsWith('/') && it.count { char -> char == '/' } >= 2 })
        assertEquals(routes.size, routes.toSet().size)
    }
}
