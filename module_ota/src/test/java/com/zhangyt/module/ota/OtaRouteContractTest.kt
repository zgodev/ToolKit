package com.zhangyt.module.ota

import com.zhangyt.common.router.RouterPath
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * @description: 固化 OTA 跨模块路由的业务分组与公开路径契约
 * @author: zhangyt
 * @Date: 2026-08-04
 */
class OtaRouteContractTest {

    @Test
    fun `ota activity route stays inside ota group`() {
        val route = RouterPath.Ota.ACTIVITY_OTA

        assertTrue(route.startsWith("/ota/"))
        assertEquals("/ota/activity_ota", route)
    }
}
