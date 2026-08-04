data class ProjectDependencyEdge(
    val from: String,
    val configuration: String,
    val target: String,
)

data class ModuleReadmeIndex(
    val sourcePath: String,
    val headings: Set<String>,
)

data class ArchitectureSnapshot(
    val modulePaths: List<String> = emptyList(),
    val missingBuildFiles: List<String> = emptyList(),
    val dependencies: List<ProjectDependencyEdge> = emptyList(),
    val readmes: Map<String, ModuleReadmeIndex> = emptyMap(),
    val resources: List<ResourceIndexEntry> = emptyList(),
    val sourceFilePaths: List<String> = emptyList(),
    val projectIndexMatches: Boolean = true,
    val resourcePrefixExceptions: Set<String> = emptySet(),
    val standaloneFeature: String? = null,
)

data class ArchitectureViolation(
    val ruleId: String,
    val location: String,
    val message: String,
)

object ArchitectureRules {
    val requiredReadmeSections = linkedSetOf(
        "模块职责",
        "模块类型",
        "依赖规则",
        "目录结构",
        "公共入口",
        "新代码放置",
        "验证命令",
    )

    private val standaloneFeatureEdges = mapOf(
        (":module_mine" to ":module_ota") to "mine",
    )

    fun validate(snapshot: ArchitectureSnapshot): List<ArchitectureViolation> = buildList {
        snapshot.dependencies
            .distinct()
            .sortedWith(compareBy(ProjectDependencyEdge::from, ProjectDependencyEdge::target))
            .forEach { edge ->
                when {
                    edge.from.startsWith(":core:") && edge.target.startsWith(":module_") -> add(
                        ArchitectureViolation(
                            ruleId = "ARCH_DEP_CORE_FEATURE",
                            location = edge.from,
                            message = "core 模块不能依赖业务模块：${edge.from} → ${edge.target}。请把稳定契约下沉到 core，或由 app/feature 反向组装。",
                        )
                    )

                    moduleType(edge.from) == ModuleType.FEATURE &&
                        moduleType(edge.target) == ModuleType.FEATURE &&
                        edge.from != edge.target &&
                        !isAllowedStandaloneEdge(edge, snapshot.standaloneFeature) -> add(
                        ArchitectureViolation(
                            ruleId = "ARCH_DEP_FEATURE_FEATURE",
                            location = edge.from,
                            message = "业务模块不能直接依赖其他业务模块：${edge.from} → ${edge.target}。跨业务跳转使用 core:navigation 路由契约，由 app 组装实现。",
                        )
                    )

                    !isAllowedProjectDependency(edge, snapshot.standaloneFeature) -> add(
                        ArchitectureViolation(
                            ruleId = "ARCH_DEP_DIRECTION",
                            location = edge.from,
                            message = "依赖方向不符合模块类型白名单：${edge.from} → ${edge.target}。请通过 core 路由或稳定能力协作，或由 app 组装。",
                        )
                    )
                }
            }

        snapshot.missingBuildFiles.distinct().sorted().forEach { modulePath ->
            add(
                ArchitectureViolation(
                    ruleId = "ARCH_BUILD_FILE_MISSING",
                    location = modulePath,
                    message = "显式 include 的叶子模块缺少 build.gradle(.kts)，请补齐模块构建配置或从 settings.gradle 移除无效 include。",
                )
            )
        }

        snapshot.modulePaths
            .distinct()
            .sorted()
            .filter { it.matches(Regex(":module_[^:]+:.+")) }
            .forEach { modulePath ->
                add(
                    ArchitectureViolation(
                        ruleId = "ARCH_FEATURE_NESTED_MODULE",
                        location = modulePath,
                        message = "业务模块必须使用单层 :module_<name> 结构，不再创建 api/impl 等子模块。",
                    )
                )
            }

        snapshot.modulePaths.distinct().sorted().forEach { modulePath ->
            val readme = snapshot.readmes[modulePath]
            if (readme == null) {
                add(
                    ArchitectureViolation(
                        ruleId = "ARCH_README_MISSING",
                        location = modulePath,
                        message = "模块缺少 README.md。请按项目模块 README 模板补齐职责、边界和验证命令。",
                    )
                )
            } else {
                val missingSections = requiredReadmeSections - readme.headings
                if (missingSections.isNotEmpty()) {
                    add(
                        ArchitectureViolation(
                            ruleId = "ARCH_README_SECTION",
                            location = readme.sourcePath,
                            message = "模块 README 缺少章节：${missingSections.joinToString()}。",
                        )
                    )
                }
            }
        }

        snapshot.resources
            .distinct()
            .sortedWith(compareBy(ResourceIndexEntry::owner, ResourceIndexEntry::sourcePath, ResourceIndexEntry::name))
            .forEach { resource ->
                val prefix = expectedResourcePrefix(resource.owner) ?: return@forEach
                val exactResource = "${resource.sourcePath}#${resource.name}"
                if (resource.sourcePath !in snapshot.resourcePrefixExceptions &&
                    exactResource !in snapshot.resourcePrefixExceptions &&
                    !resource.name.startsWith(prefix)
                ) {
                    add(
                        ArchitectureViolation(
                            ruleId = "ARCH_RESOURCE_PREFIX",
                            location = resource.sourcePath,
                            message = "资源 `${resource.name}` 必须使用 `${prefix}` 所有者前缀。优先复用已有资源，不要新增例外。",
                        )
                    )
                }
            }

        if (!snapshot.projectIndexMatches) {
            add(
                ArchitectureViolation(
                    ruleId = "ARCH_INDEX_STALE",
                    location = "docs/PROJECT_INDEX.md",
                    message = "项目索引已过期。运行 ./gradlew generateProjectIndex 后重新校验。",
                )
            )
        }

        snapshot.sourceFilePaths
            .distinct()
            .sorted()
            .filter { path ->
                path.contains("ExampleUnitTest", ignoreCase = true) ||
                    path.contains("ExampleInstrumentedTest", ignoreCase = true) ||
                    path.contains("addition_isCorrect", ignoreCase = true)
            }
            .forEach { path ->
                add(
                    ArchitectureViolation(
                        ruleId = "ARCH_TEMPLATE_TEST",
                        location = path,
                        message = "删除模板测试并为真实业务契约编写测试。",
                    )
                )
            }
    }

    private fun expectedResourcePrefix(owner: String): String? = when {
        owner == ":app" -> "app_"
        owner == ":core:designsystem" -> "common_"
        owner == ":core:web" -> "web_"
        owner.matches(Regex(":module_[^:]+")) -> owner.removePrefix(":module_") + "_"
        else -> null
    }

    private fun isAllowedProjectDependency(edge: ProjectDependencyEdge, standaloneFeature: String?): Boolean {
        if (isAllowedStandaloneEdge(edge, standaloneFeature)) return true

        val target = moduleType(edge.target)
        return when (moduleType(edge.from)) {
            ModuleType.APP -> target in setOf(
                ModuleType.CORE,
                ModuleType.LEGACY,
                ModuleType.FEATURE,
            )
            ModuleType.CORE -> target == ModuleType.CORE
            ModuleType.LEGACY -> target == ModuleType.CORE || target == ModuleType.LEGACY
            ModuleType.FEATURE -> target == ModuleType.CORE
            ModuleType.OTHER -> false
        }
    }

    private fun isAllowedStandaloneEdge(edge: ProjectDependencyEdge, standaloneFeature: String?): Boolean =
        standaloneFeatureEdges[edge.from to edge.target]?.let { it == standaloneFeature } == true

    private fun moduleType(path: String): ModuleType = when {
        path == ":app" -> ModuleType.APP
        path.startsWith(":core:") -> ModuleType.CORE
        path.startsWith(":legacy:") -> ModuleType.LEGACY
        path.matches(Regex(":module_[^:]+")) -> ModuleType.FEATURE
        else -> ModuleType.OTHER
    }

    private enum class ModuleType {
        APP,
        CORE,
        LEGACY,
        FEATURE,
        OTHER,
    }
}
