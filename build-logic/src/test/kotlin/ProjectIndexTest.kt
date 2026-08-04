import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ProjectIndexTest {

    @Test
    fun `render sorts every section deterministically`() {
        val module = ModuleIndexEntry(
            path = ":module_home",
            kind = "feature",
            namespace = "com.zhangyt.module.home",
            summary = "首页业务模块。",
            readmePath = "module_home/README.md",
            dependencies = listOf(
                DependencyIndexEntry("implementation", ":core:navigation"),
                DependencyIndexEntry("api", ":core:model"),
            ),
            publicEntries = listOf(
                PublicEntryIndex("ChatFragment", "module_home/src/main/java/ChatFragment.kt"),
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
                ResourceIndexEntry(":module_home", "layout", "home_fragment_chat", "z.xml"),
                ResourceIndexEntry(":app", "layout", "app_activity_main", "a.xml"),
                ResourceIndexEntry(":app", "string", "app_name", "values/strings.xml"),
                ResourceIndexEntry(":app", "string", "app_name", "values-en/strings.xml"),
            ),
            routes = listOf(
                RouteIndexEntry(":core:navigation", "/home/fragment_chat", "RouterPath.Home.FRAGMENT_CHAT", "b.kt"),
                RouteIndexEntry(":core:navigation", "/main/activity_main", "RouterPath.Home.ACTIVITY_MAIN", "a.kt"),
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
        assertTrue(first.indexOf(":app") < first.indexOf(":module_home"))
        assertTrue(first.indexOf("app_activity_main") < first.indexOf("home_fragment_chat"))
        assertTrue(first.indexOf("/home/fragment_chat") < first.indexOf("/main/activity_main"))
        assertTrue(first.indexOf("standalone=home") < first.indexOf("standalone=login"))
        assertTrue(first.contains(":module_home:assembleDebug -Pstandalone=home"))
        assertTrue(!first.contains(":module_home:impl"))
    }

    @Test
    fun `render removes duplicate resources and routes`() {
        val resource = ResourceIndexEntry(":app", "layout", "app_activity_main", "app.xml")
        val route = RouteIndexEntry(":app", "/main/activity_main", "RouterPath.Home.ACTIVITY_MAIN", "MainActivity.kt")
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
