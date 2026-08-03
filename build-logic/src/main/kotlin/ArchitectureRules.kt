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

    private val standaloneImplementationEdges = mapOf(
        (":module_mine:impl" to ":module_ota:impl") to "mine",
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

                    edge.from.matches(Regex(":module_[^:]+:api")) && edge.target.endsWith(":impl") -> add(
                        ArchitectureViolation(
                            ruleId = "ARCH_DEP_API_IMPL",
                            location = edge.from,
                            message = "feature api 不能依赖实现模块：${edge.from} → ${edge.target}。只保留稳定契约。",
                        )
                    )

                    edge.from.matches(Regex(":module_[^:]+:impl")) &&
                        edge.target.matches(Regex(":module_[^:]+:impl")) &&
                        edge.from != edge.target &&
                        !isAllowedStandaloneEdge(edge, snapshot.standaloneFeature) -> add(
                        ArchitectureViolation(
                            ruleId = "ARCH_DEP_IMPL_IMPL",
                            location = edge.from,
                            message = "feature 实现不能直接依赖其他 feature 实现：${edge.from} → ${edge.target}。改为依赖对方 api，由 app 组装。",
                        )
                    )

                    !isAllowedProjectDependency(edge, snapshot.standaloneFeature) -> add(
                        ArchitectureViolation(
                            ruleId = "ARCH_DEP_DIRECTION",
                            location = edge.from,
                            message = "依赖方向不符合模块类型白名单：${edge.from} → ${edge.target}。请通过 feature api 协作，或把能力放入职责匹配的 core/app 组装层。",
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
        owner.matches(Regex(":module_[^:]+:(api|impl)")) ->
            owner.substringAfter(":module_").substringBefore(':') + "_"
        else -> null
    }

    private fun isAllowedProjectDependency(edge: ProjectDependencyEdge, standaloneFeature: String?): Boolean {
        if (isAllowedStandaloneEdge(edge, standaloneFeature)) return true

        val target = moduleType(edge.target)
        return when (moduleType(edge.from)) {
            ModuleType.APP -> target in setOf(
                ModuleType.CORE,
                ModuleType.LEGACY,
                ModuleType.FEATURE_API,
                ModuleType.FEATURE_IMPL,
            )
            ModuleType.CORE -> target == ModuleType.CORE
            ModuleType.LEGACY -> target == ModuleType.CORE || target == ModuleType.LEGACY
            ModuleType.FEATURE_API -> target == ModuleType.CORE || target == ModuleType.FEATURE_API
            ModuleType.FEATURE_IMPL -> target == ModuleType.CORE || target == ModuleType.FEATURE_API
            ModuleType.OTHER -> false
        }
    }

    private fun isAllowedStandaloneEdge(edge: ProjectDependencyEdge, standaloneFeature: String?): Boolean =
        standaloneImplementationEdges[edge.from to edge.target]?.let { it == standaloneFeature } == true

    private fun moduleType(path: String): ModuleType = when {
        path == ":app" -> ModuleType.APP
        path.startsWith(":core:") -> ModuleType.CORE
        path.startsWith(":legacy:") -> ModuleType.LEGACY
        path.matches(Regex(":module_[^:]+:api")) -> ModuleType.FEATURE_API
        path.matches(Regex(":module_[^:]+:impl")) -> ModuleType.FEATURE_IMPL
        else -> ModuleType.OTHER
    }

    private enum class ModuleType {
        APP,
        CORE,
        LEGACY,
        FEATURE_API,
        FEATURE_IMPL,
        OTHER,
    }
}
