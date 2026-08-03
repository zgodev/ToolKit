package com.zhangyt.toolkit

import com.zhangyt.common.router.RouterPath
import com.zhangyt.core.web.databinding.WebActivityBinding
import com.zhangyt.module.home.api.HomeRoutes
import com.zhangyt.module.login.api.LoginRoutes
import com.zhangyt.module.mine.api.MineRoutes
import com.zhangyt.module.ota.api.OtaRoutes
import com.zhangyt.module.ota.databinding.OtaActivityUpdateBinding
import com.zhangyt.toolkit.databinding.AppActivityMainBinding
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

    @Test
    fun `app shell resources use app ownership prefix`() {
        assertTrue(R.layout.app_activity_main != 0)
        assertTrue(R.menu.app_menu_bottom != 0)
        assertTrue(R.color.app_color_tab != 0)
    }

    @Test
    fun `renamed layouts generate stable binding contracts`() {
        assertEquals("AppActivityMainBinding", AppActivityMainBinding::class.java.simpleName)
        assertEquals("WebActivityBinding", WebActivityBinding::class.java.simpleName)
        assertEquals("OtaActivityUpdateBinding", OtaActivityUpdateBinding::class.java.simpleName)
    }
}
