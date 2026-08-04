package com.zhangyt.toolkit

import com.zhangyt.common.router.RouterPath
import com.zhangyt.core.web.databinding.WebActivityBinding
import com.zhangyt.module.ota.databinding.OtaActivityUpdateBinding
import com.zhangyt.toolkit.databinding.AppActivityMainBinding
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class RouteContractTest {

    private val routes = listOf(
        RouterPath.Login.ACTIVITY_LOGIN,
        RouterPath.Home.ACTIVITY_MAIN,
        RouterPath.Home.FRAGMENT_CHAT,
        RouterPath.Home.FRAGMENT_CONTACTS,
        RouterPath.Home.FRAGMENT_DISCOVER,
        RouterPath.Mine.FRAGMENT_MINE,
        RouterPath.Ota.ACTIVITY_OTA,
        RouterPath.Common.ACTIVITY_WEB,
    )

    @Test
    fun `all routes are absolute and globally unique`() {
        assertTrue(routes.all { it.startsWith('/') && it.count { char -> char == '/' } >= 2 })
        assertEquals(routes.size, routes.toSet().size)
    }

    @Test
    fun `route values remain backwards compatible`() {
        assertEquals(
            listOf(
                "/login/activity_login",
                "/main/activity_main",
                "/home/fragment_chat",
                "/home/fragment_contacts",
                "/home/fragment_discover",
                "/mine/fragment_mine",
                "/ota/activity_ota",
                "/common/activity_web",
            ),
            routes,
        )
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
