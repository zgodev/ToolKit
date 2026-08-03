import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ProjectIndexTest {

    @Test
    fun `render sorts every section deterministically`() {
        val module = ModuleIndexEntry(
            path = ":module_home:api",
            kind = "feature-api",
            namespace = "com.zhangyt.module.home.api",
            summary = "首页跨模块路由契约。",
            readmePath = "module_home/api/README.md",
            dependencies = listOf(
                DependencyIndexEntry("implementation", ":core:navigation"),
                DependencyIndexEntry("api", ":core:model"),
            ),
            publicEntries = listOf(
                PublicEntryIndex("HomeRoutes", "module_home/api/src/main/java/HomeRoutes.kt"),
            ),
        )
        val snapshot = ProjectIndexSnapshot(
            modules = listOf(
                module,
                ModuleIndexEntry(
                    path = ":app",
                    kind = "app",
                    namespace = "com.zhangyt.toolkit",
                    summary = "完整应用组装层。",
                    readmePath = "app/README.md",
                ),
            ),
            resources = listOf(
                ResourceIndexEntry(":module_home:impl", "layout", "home_fragment_chat", "z.xml"),
                ResourceIndexEntry(":app", "layout", "app_activity_main", "a.xml"),
                ResourceIndexEntry(":app", "string", "app_name", "values/strings.xml"),
                ResourceIndexEntry(":app", "string", "app_name", "values-en/strings.xml"),
            ),
            routes = listOf(
                RouteIndexEntry(":module_home:api", "/home/fragment_chat", "HomeRoutes.FRAGMENT_CHAT", "b.kt"),
                RouteIndexEntry(":app", "/main/activity_main", "HomeRoutes.ACTIVITY_MAIN", "a.kt"),
                RouteIndexEntry(":app", "/same/path", "Routes.SAME", "z.kt"),
                RouteIndexEntry(":app", "/same/path", "Routes.SAME", "a.kt"),
            ),
            standaloneCommands = listOf("home", "login"),
        )

        val first = ProjectIndexRenderer.render(snapshot)
        val second = ProjectIndexRenderer.render(
            snapshot.copy(
                modules = snapshot.modules.reversed(),
                resources = snapshot.resources.reversed(),
                routes = snapshot.routes.reversed(),
                standaloneCommands = snapshot.standaloneCommands.reversed(),
            )
        )

        assertEquals(first, second)
        assertTrue(first.endsWith("\n"))
        assertTrue(first.indexOf(":app") < first.indexOf(":module_home:api"))
        assertTrue(first.indexOf("app_activity_main") < first.indexOf("home_fragment_chat"))
        assertTrue(first.indexOf("/home/fragment_chat") < first.indexOf("/main/activity_main"))
        assertTrue(first.indexOf("standalone=home") < first.indexOf("standalone=login"))
    }

    @Test
    fun `render removes duplicate resources and routes`() {
        val resource = ResourceIndexEntry(":app", "layout", "app_activity_main", "app.xml")
        val route = RouteIndexEntry(":app", "/main/activity_main", "HomeRoutes.ACTIVITY_MAIN", "MainActivity.kt")
        val rendered = ProjectIndexRenderer.render(
            ProjectIndexSnapshot(
                resources = listOf(resource, resource),
                routes = listOf(route, route),
            )
        )

        assertEquals(1, Regex("app_activity_main").findAll(rendered).count())
        assertEquals(1, Regex("/main/activity_main").findAll(rendered).count())
    }
}
