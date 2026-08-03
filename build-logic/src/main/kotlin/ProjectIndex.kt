data class DependencyIndexEntry(
    val configuration: String,
    val target: String,
)

data class PublicEntryIndex(
    val name: String,
    val sourcePath: String,
)

data class ModuleIndexEntry(
    val path: String,
    val kind: String,
    val namespace: String = "-",
    val summary: String,
    val readmePath: String,
    val dependencies: List<DependencyIndexEntry> = emptyList(),
    val publicEntries: List<PublicEntryIndex> = emptyList(),
)

data class ResourceIndexEntry(
    val owner: String,
    val type: String,
    val name: String,
    val sourcePath: String,
)

data class RouteIndexEntry(
    val owner: String,
    val route: String,
    val constantName: String,
    val sourcePath: String,
)

data class ProjectIndexSnapshot(
    val modules: List<ModuleIndexEntry> = emptyList(),
    val resources: List<ResourceIndexEntry> = emptyList(),
    val routes: List<RouteIndexEntry> = emptyList(),
    val standaloneCommands: List<String> = emptyList(),
)

object ProjectIndexRenderer {
    fun render(snapshot: ProjectIndexSnapshot): String = buildString {
        appendLine("# ToolKit 项目索引")
        appendLine()
        appendLine("> 此文件由 `./gradlew generateProjectIndex` 生成。不要手工编辑；结构变更后重新生成。")
        appendLine()
        appendLine("## 模块")

        snapshot.modules
            .distinctBy { it.path }
            .sortedBy { it.path }
            .forEach { module ->
                appendLine()
                appendLine("### `${escape(module.path)}`")
                appendLine()
                appendLine("- 类型：`${escape(module.kind)}`")
                appendLine("- Namespace：`${escape(module.namespace)}`")
                appendLine("- 职责：${escape(module.summary)}")
                appendLine("- 说明：[`${escape(module.readmePath)}`](../${escapeLink(module.readmePath)})")

                if (module.dependencies.isNotEmpty()) {
                    appendLine("- 项目依赖：")
                    module.dependencies
                        .distinct()
                        .sortedWith(compareBy(DependencyIndexEntry::target, DependencyIndexEntry::configuration))
                        .forEach { dependency ->
                            appendLine("  - `${escape(dependency.configuration)}` → `${escape(dependency.target)}`")
                        }
                }

                if (module.publicEntries.isNotEmpty()) {
                    appendLine("- 公共入口：")
                    module.publicEntries
                        .distinct()
                        .sortedWith(compareBy(PublicEntryIndex::name, PublicEntryIndex::sourcePath))
                        .forEach { entry ->
                            appendLine("  - `${escape(entry.name)}` — `${escape(entry.sourcePath)}`")
                        }
                }
            }

        appendLine()
        appendLine("## 路由")
        appendLine()
        appendLine("| 路径 | 所属模块 | 常量 | 声明位置 |")
        appendLine("| --- | --- | --- | --- |")
        snapshot.routes
            .distinct()
            .sortedWith(
                compareBy(
                    RouteIndexEntry::route,
                    RouteIndexEntry::owner,
                    RouteIndexEntry::constantName,
                    RouteIndexEntry::sourcePath,
                )
            )
            .forEach { route ->
                appendLine(
                    "| `${escape(route.route)}` | `${escape(route.owner)}` | `${escape(route.constantName)}` | `${escape(route.sourcePath)}` |"
                )
            }

        appendLine()
        appendLine("## Android 资源")
        appendLine()
        appendLine("| 名称 | 类型 | 所属模块 | 文件 |")
        appendLine("| --- | --- | --- | --- |")
        snapshot.resources
            .distinct()
            .sortedWith(
                compareBy(
                    ResourceIndexEntry::name,
                    ResourceIndexEntry::owner,
                    ResourceIndexEntry::type,
                    ResourceIndexEntry::sourcePath,
                )
            )
            .forEach { resource ->
                appendLine(
                    "| `${escape(resource.name)}` | `${escape(resource.type)}` | `${escape(resource.owner)}` | `${escape(resource.sourcePath)}` |"
                )
            }

        appendLine()
        appendLine("## 独立组件构建")
        snapshot.standaloneCommands
            .distinct()
            .sorted()
            .forEach { feature ->
                appendLine()
                appendLine("- `./gradlew :module_${escape(feature)}:impl:assembleDebug -Pstandalone=${escape(feature)}`")
            }
    }

    private fun escape(value: String): String = value.replace("|", "\\|")

    private fun escapeLink(value: String): String = value.replace(" ", "%20")
}
