import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ArchitectureRulesTest {

    @Test
    fun `core cannot depend on feature`() {
        val violations = ArchitectureRules.validate(
            snapshot(dependencies = listOf(ProjectDependencyEdge(":core:ui", "implementation", ":module_home")))
        )

        assertEquals(listOf("ARCH_DEP_CORE_FEATURE"), violations.map { it.ruleId })
    }

    @Test
    fun `feature cannot depend on another feature`() {
        val violations = ArchitectureRules.validate(
            snapshot(
                dependencies = listOf(
                    ProjectDependencyEdge(":module_home", "implementation", ":module_login")
                )
            )
        )

        assertEquals(listOf("ARCH_DEP_FEATURE_FEATURE"), violations.map { it.ruleId })
    }

    @Test
    fun `nested feature modules are rejected`() {
        val violations = ArchitectureRules.validate(
            snapshot(
                modulePaths = listOf(":module_home:api", ":module_home:impl"),
            )
        )

        assertEquals(
            listOf("ARCH_FEATURE_NESTED_MODULE", "ARCH_FEATURE_NESTED_MODULE"),
            violations.map { it.ruleId }.filter { it == "ARCH_FEATURE_NESTED_MODULE" },
        )
    }

    @Test
    fun `mine standalone may assemble ota implementation`() {
        val violations = ArchitectureRules.validate(
            snapshot(
                dependencies = listOf(
                    ProjectDependencyEdge(":module_mine", "implementation", ":module_ota")
                ),
                standaloneFeature = "mine",
            )
        )

        assertTrue(violations.isEmpty())
    }

    @Test
    fun `mine regular module cannot depend on ota module`() {
        val violations = ArchitectureRules.validate(
            snapshot(
                dependencies = listOf(
                    ProjectDependencyEdge(":module_mine", "implementation", ":module_ota")
                ),
            )
        )

        assertEquals(listOf("ARCH_DEP_FEATURE_FEATURE"), violations.map { it.ruleId })
    }

    @Test
    fun `project dependencies follow module type allowlist`() {
        val violations = ArchitectureRules.validate(
            snapshot(
                dependencies = listOf(
                    ProjectDependencyEdge(":core:ui", "implementation", ":legacy:utils"),
                    ProjectDependencyEdge(":module_home", "implementation", ":app"),
                    ProjectDependencyEdge(":module_home", "implementation", ":legacy:utils"),
                    ProjectDependencyEdge(":legacy:network", "implementation", ":module_home"),
                    ProjectDependencyEdge(":legacy:network", "implementation", ":core:network"),
                    ProjectDependencyEdge(":app", "implementation", ":module_home"),
                ),
            )
        )

        assertEquals(
            listOf(
                "ARCH_DEP_DIRECTION",
                "ARCH_DEP_DIRECTION",
                "ARCH_DEP_DIRECTION",
                "ARCH_DEP_DIRECTION",
            ),
            violations.map { it.ruleId },
        )
    }

    @Test
    fun `every module readme requires standard sections`() {
        val missing = ArchitectureRules.validate(
            ArchitectureSnapshot(
                modulePaths = listOf(":app", ":core:ui", "build-logic"),
                readmes = mapOf(
                    ":app" to ModuleReadmeIndex("app/README.md", ArchitectureRules.requiredReadmeSections),
                    ":core:ui" to ModuleReadmeIndex("core/ui/README.md", setOf("模块职责")),
                ),
                projectIndexMatches = true,
            )
        )

        assertEquals(
            listOf("ARCH_README_SECTION", "ARCH_README_MISSING"),
            missing.map { it.ruleId },
        )
    }

    @Test
    fun `included leaf module requires a build file`() {
        val violations = ArchitectureRules.validate(
            ArchitectureSnapshot(
                modulePaths = listOf(":broken"),
                readmes = mapOf(
                    ":broken" to ModuleReadmeIndex(
                        "broken/README.md",
                        ArchitectureRules.requiredReadmeSections,
                    )
                ),
                missingBuildFiles = listOf(":broken"),
            )
        )

        assertEquals(listOf("ARCH_BUILD_FILE_MISSING"), violations.map { it.ruleId })
    }

    @Test
    fun `owned resources require stable prefix unless frozen exception`() {
        val snapshot = snapshot(
            resources = listOf(
                ResourceIndexEntry(":module_login", "layout", "screen_login", "bad.xml"),
                ResourceIndexEntry(":module_login", "drawable", "login_bg_button", "good.xml"),
                ResourceIndexEntry(":app", "mipmap", "ic_launcher", "launcher.xml"),
            ),
            resourcePrefixExceptions = setOf("launcher.xml"),
        )

        val violations = ArchitectureRules.validate(snapshot)

        assertEquals(listOf("ARCH_RESOURCE_PREFIX"), violations.map { it.ruleId })
        assertTrue(violations.single().location.contains("bad.xml"))
    }

    @Test
    fun `template tests and stale project index fail`() {
        val violations = ArchitectureRules.validate(
            snapshot(
                sourceFilePaths = listOf(
                    "app/src/test/ExampleUnitTest.java",
                    "app/src/androidTest/ExampleInstrumentedTest.java",
                ),
                projectIndexMatches = false,
            )
        )

        assertEquals(
            listOf("ARCH_INDEX_STALE", "ARCH_TEMPLATE_TEST", "ARCH_TEMPLATE_TEST"),
            violations.map { it.ruleId },
        )
    }

    @Test
    fun `source types without required class header are rejected`() {
        val violations = ArchitectureRules.validate(
            snapshot(
                sourceFilePaths = listOf(
                    "module_order/src/main/java/com/zhangyt/order/OrderRepository.kt#classHeader:OrderRepository",
                ),
            )
        )

        assertEquals(listOf("ARCH_CLASS_HEADER"), violations.map { it.ruleId })
    }

    private fun snapshot(
        modulePaths: List<String> = emptyList(),
        dependencies: List<ProjectDependencyEdge> = emptyList(),
        resources: List<ResourceIndexEntry> = emptyList(),
        sourceFilePaths: List<String> = emptyList(),
        projectIndexMatches: Boolean = true,
        resourcePrefixExceptions: Set<String> = emptySet(),
        standaloneFeature: String? = null,
    ) = ArchitectureSnapshot(
        modulePaths = modulePaths,
        dependencies = dependencies,
        resources = resources,
        sourceFilePaths = sourceFilePaths,
        projectIndexMatches = projectIndexMatches,
        resourcePrefixExceptions = resourcePrefixExceptions,
        standaloneFeature = standaloneFeature,
    )
}
