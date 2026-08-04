import org.gradle.testfixtures.ProjectBuilder
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder

class ArchitectureGuardPluginTest {

    @get:Rule
    val temporaryFolder = TemporaryFolder()

    @Test
    fun `plugin registers project governance tasks`() {
        val project = ProjectBuilder.builder().withName("ToolKit").build()

        project.pluginManager.apply(ArchitectureGuardPlugin::class.java)

        assertNotNull(project.tasks.findByName("generateProjectIndex"))
        assertNotNull(project.tasks.findByName("verifyProjectIndex"))
        assertNotNull(project.tasks.findByName("verifyArchitecture"))
        assertEquals("documentation", project.tasks.getByName("generateProjectIndex").group)
        assertEquals("verification", project.tasks.getByName("verifyProjectIndex").group)
        assertEquals("verification", project.tasks.getByName("verifyArchitecture").group)
    }

    @Test
    fun `collector marks top level type without required class header`() {
        val projectDir = temporaryFolder.newFolder("ToolKit")
        val source = projectDir.resolve("build-logic/src/main/kotlin/OrderRepository.kt")
        source.parentFile.mkdirs()
        source.writeText("class OrderRepository")
        val project = ProjectBuilder.builder().withProjectDir(projectDir).withName("ToolKit").build()

        val snapshot = ProjectGovernanceCollector.collectArchitecture(
            rootProject = project,
            index = ProjectIndexSnapshot(),
            projectIndexMatches = true,
        )

        assertTrue(
            snapshot.sourceFilePaths.contains(
                "build-logic/src/main/kotlin/OrderRepository.kt#classHeader:OrderRepository"
            )
        )
    }

    @Test
    fun `collector accepts complete header and freezes historical baseline`() {
        val projectDir = temporaryFolder.newFolder("ToolKitWithBaseline")
        val sourceDir = projectDir.resolve("build-logic/src/main/kotlin").apply { mkdirs() }
        sourceDir.resolve("NewOrderRepository.kt").writeText(
            """
            /**
             * @description: 负责订单数据访问
             * @author: z
             * @Date: 2026-08-04
             */
            class NewOrderRepository
            """.trimIndent()
        )
        sourceDir.resolve("LegacyRepository.kt").writeText("class LegacyRepository")
        val baseline = projectDir.resolve("config/architecture/class-header-baseline.txt")
        baseline.parentFile.mkdirs()
        baseline.writeText(
            "build-logic/src/main/kotlin/LegacyRepository.kt#classHeader:LegacyRepository\n"
        )
        val project = ProjectBuilder.builder().withProjectDir(projectDir).withName("ToolKit").build()

        val snapshot = ProjectGovernanceCollector.collectArchitecture(
            rootProject = project,
            index = ProjectIndexSnapshot(),
            projectIndexMatches = true,
        )

        assertTrue(snapshot.sourceFilePaths.none { "#classHeader:" in it })
    }

    @Test
    fun `collector rejects ordinary kdoc without required fields`() {
        val projectDir = temporaryFolder.newFolder("ToolKitWithOrdinaryKdoc")
        val source = projectDir.resolve("build-logic/src/main/kotlin/OrderManager.kt")
        source.parentFile.mkdirs()
        source.writeText(
            """
            /**
             * 订单管理器。
             *
             * 使用示例：
             * ```
             * when (state) { Ready -> { /* 开始处理 */ } }
             * }
             * ```
             */
            class OrderManager
            """.trimIndent()
        )
        val project = ProjectBuilder.builder().withProjectDir(projectDir).withName("ToolKit").build()

        val snapshot = ProjectGovernanceCollector.collectArchitecture(
            rootProject = project,
            index = ProjectIndexSnapshot(),
            projectIndexMatches = true,
        )

        assertTrue(
            snapshot.sourceFilePaths.contains(
                "build-logic/src/main/kotlin/OrderManager.kt#classHeader:OrderManager"
            )
        )
    }
}
