import org.gradle.testfixtures.ProjectBuilder
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder

class ProjectGovernanceCollectorTest {
    @get:Rule
    val temporaryFolder = TemporaryFolder()

    @Test
    fun `index excludes hierarchy-only aggregator projects`() {
        val root = ProjectBuilder.builder()
            .withName("root")
            .withProjectDir(temporaryFolder.root)
            .build()
        val coreDirectory = temporaryFolder.newFolder("core")
        val core = ProjectBuilder.builder()
            .withName("core")
            .withParent(root)
            .withProjectDir(coreDirectory)
            .build()
        val uiDirectory = temporaryFolder.newFolder("core", "ui")
        uiDirectory.resolve("build.gradle").writeText("plugins { id 'java-library' }")
        val ui = ProjectBuilder.builder()
            .withName("ui")
            .withParent(core)
            .withProjectDir(uiDirectory)
            .build()
        val modelDirectory = temporaryFolder.newFolder("core", "model")
        modelDirectory.resolve("build.gradle").writeText("plugins { id 'java-library' }")
        val model = ProjectBuilder.builder()
            .withName("model")
            .withParent(core)
            .withProjectDir(modelDirectory)
            .build()
        val missingDirectory = temporaryFolder.newFolder("missing")
        ProjectBuilder.builder()
            .withName("missing")
            .withParent(root)
            .withProjectDir(missingDirectory)
            .build()
        val sourceDirectory = uiDirectory.resolve("src/main/kotlin").apply { mkdirs() }
        sourceDirectory.resolve("Sample.kt").writeText(
            """
            class VisibleEntry
            internal class InternalEntry
            private object PrivateEntry
            fun visibleFunction() = Unit
            private fun privateFunction() = Unit

            object Routes {
                object Common {
                    const val PAGE = "/common/page"
                }
            }
            """.trimIndent()
        )
        val valuesDirectory = uiDirectory.resolve("src/main/common-res/values").apply { mkdirs() }
        valuesDirectory.resolve("strings.xml").writeText(
            "<resources><string name=\"ui_sample_title\">Sample</string></resources>"
        )
        ui.configurations.create("implementation").dependencies.add(
            ui.dependencies.project(mapOf("path" to model.path))
        )
        ui.configurations.create("debugUnitTestCompileClasspath").dependencies.add(
            ui.dependencies.project(mapOf("path" to model.path))
        )

        val index = ProjectGovernanceCollector.collectIndex(root)

        assertEquals(listOf(":core:model", ":core:ui", ":missing"), index.modules.map { it.path })
        assertEquals(
            listOf(DependencyIndexEntry("implementation", ":core:model")),
            index.modules.single { it.path == ":core:ui" }.dependencies,
        )
        assertEquals(
            setOf("Routes", "VisibleEntry", "visibleFunction"),
            index.modules.single { it.path == ":core:ui" }.publicEntries.map { it.name }.toSet(),
        )
        assertEquals(
            listOf(RouteIndexEntry(":core:ui", "/common/page", "Routes.Common.PAGE", "core/ui/src/main/kotlin/Sample.kt")),
            index.routes,
        )
        assertTrue(
            index.resources.contains(
                ResourceIndexEntry(
                    ":core:ui",
                    "string",
                    "ui_sample_title",
                    "core/ui/src/main/common-res/values/strings.xml",
                )
            )
        )
    }

    @Test
    fun `standalone assembly edge is normalized out of index but retained for architecture`() {
        val root = ProjectBuilder.builder()
            .withName("root")
            .withProjectDir(temporaryFolder.root)
            .build()
        val mineDirectory = temporaryFolder.newFolder("module_mine")
        mineDirectory.resolve("build.gradle").writeText("// feature")
        val standaloneManifest = mineDirectory.resolve("src/standalone/AndroidManifest.xml")
        standaloneManifest.parentFile.mkdirs()
        standaloneManifest.writeText("<manifest />")
        val mine = ProjectBuilder.builder()
            .withName("module_mine")
            .withParent(root)
            .withProjectDir(mineDirectory)
            .build()
        val otaDirectory = temporaryFolder.newFolder("module_ota")
        otaDirectory.resolve("build.gradle").writeText("// feature")
        val ota = ProjectBuilder.builder()
            .withName("module_ota")
            .withParent(root)
            .withProjectDir(otaDirectory)
            .build()
        mine.configurations.create("implementation").dependencies.add(
            mine.dependencies.project(mapOf("path" to ota.path))
        )

        val index = ProjectGovernanceCollector.collectIndex(root)
        val architecture = ProjectGovernanceCollector.collectArchitecture(root, index, projectIndexMatches = true)

        assertTrue(index.modules.single { it.path == mine.path }.dependencies.isEmpty())
        assertEquals(listOf("mine"), index.standaloneCommands)
        assertTrue(
            architecture.dependencies.contains(
                ProjectDependencyEdge(mine.path, "implementation", ota.path)
            )
        )
    }
}
