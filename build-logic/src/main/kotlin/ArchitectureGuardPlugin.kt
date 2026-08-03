import org.gradle.api.GradleException
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.artifacts.ProjectDependency
import java.io.File

class ArchitectureGuardPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            require(this == rootProject) { "toolkit.architecture-guard can only be applied to the root project" }

            tasks.register("generateProjectIndex") {
                group = "documentation"
                description = "Generates docs/PROJECT_INDEX.md from the current module, route and resource structure."
                doLast {
                    val output = rootDir.resolve("docs/PROJECT_INDEX.md")
                    output.parentFile.mkdirs()
                    output.writeText(ProjectIndexRenderer.render(ProjectGovernanceCollector.collectIndex(rootProject)))
                    logger.lifecycle("Generated ${output.relativeTo(rootDir)}")
                }
            }

            tasks.register("verifyProjectIndex") {
                group = "verification"
                description = "Checks that docs/PROJECT_INDEX.md matches the current project structure."
                doLast {
                    val output = rootDir.resolve("docs/PROJECT_INDEX.md")
                    val expected = ProjectIndexRenderer.render(ProjectGovernanceCollector.collectIndex(rootProject))
                    if (!output.isFile || output.readText() != expected) {
                        throw GradleException(
                            "docs/PROJECT_INDEX.md is stale or missing. Run ./gradlew generateProjectIndex and commit the result."
                        )
                    }
                }
            }

            tasks.register("verifyArchitecture") {
                group = "verification"
                description = "Checks ToolKit module boundaries, README contracts, resource prefixes and index freshness."
                doLast {
                    val index = ProjectGovernanceCollector.collectIndex(rootProject)
                    val output = rootDir.resolve("docs/PROJECT_INDEX.md")
                    val expected = ProjectIndexRenderer.render(index)
                    val snapshot = ProjectGovernanceCollector.collectArchitecture(
                        rootProject = rootProject,
                        index = index,
                        projectIndexMatches = output.isFile && output.readText() == expected,
                    )
                    val violations = ArchitectureRules.validate(snapshot)
                    if (violations.isNotEmpty()) {
                        val report = violations.joinToString(separator = "\n") { violation ->
                            "[${violation.ruleId}] ${violation.location}: ${violation.message}"
                        }
                        throw GradleException("Architecture verification failed:\n$report")
                    }
                    logger.lifecycle("Architecture verification passed (${index.modules.size} modules).")
                }
            }
        }
    }
}

internal object ProjectGovernanceCollector {
    private val namespaceRegex = Regex("namespace\\s*(?:=\\s*)?['\"]([^'\"]+)['\"]")
    private val routeRegex = Regex("const\\s+val\\s+([A-Z0-9_]+)\\s*=\\s*\"(/[^\"]+)\"")
    private val kotlinObjectRegex = Regex("\\bobject\\s+([A-Za-z_][A-Za-z0-9_]*)\\s*\\{")
    private val kotlinTypeRegex = Regex(
        "^(?:(?:public|open|abstract|sealed|data|enum|annotation|value|fun)\\s+)*(?:class|interface|object)\\s+([A-Za-z_][A-Za-z0-9_]*)"
    )
    private val kotlinFunctionRegex = Regex(
        "^(?:public\\s+)?(?:(?:suspend|inline|operator|infix|tailrec|external)\\s+)*fun\\s+(?:<[^>]+>\\s*)?(?:[A-Za-z0-9_.<>?]+\\.)?([A-Za-z_][A-Za-z0-9_]*)\\s*\\("
    )
    private val kotlinTypeAliasRegex = Regex("^(?:public\\s+)?typealias\\s+([A-Za-z_][A-Za-z0-9_]*)")
    private val javaPublicTypeRegex = Regex(
        "^public\\s+(?:(?:final|abstract|sealed|non-sealed)\\s+)*(?:class|interface|enum|record|@interface)\\s+([A-Za-z_][A-Za-z0-9_]*)"
    )
    private val headingRegex = Regex("^##\\s+(.+?)\\s*$", setOf(RegexOption.MULTILINE))
    private val valueResourceRegex = Regex(
        "<(string|color|dimen|style|attr|declare-styleable|bool|integer|string-array|array|plurals)\\s+[^>]*name=\"([^\"]+)\""
    )
    private val declaredDependencyConfigurationRegex =
        Regex("(?:api|implementation|compileOnly|runtimeOnly)$", RegexOption.IGNORE_CASE)

    fun collectIndex(rootProject: Project): ProjectIndexSnapshot {
        val androidModules = rootProject.concreteSubprojects()
        val modules = androidModules.sortedBy { it.path }.map(::collectModule).toMutableList()
        collectBuildLogic(rootProject)?.let(modules::add)
        val resources = androidModules.flatMap { collectResources(rootProject, it) }
        val routes = androidModules.flatMap { collectRoutes(rootProject, it) }
        val standalone = androidModules
            .filter { it.path.matches(Regex(":module_[^:]+:impl")) }
            .filter { it.projectDir.resolve("src/standalone/AndroidManifest.xml").isFile }
            .map { it.path.substringAfter(":module_").substringBefore(':') }

        return ProjectIndexSnapshot(
            modules = modules,
            resources = resources,
            routes = routes,
            standaloneCommands = standalone,
        )
    }

    fun collectArchitecture(
        rootProject: Project,
        index: ProjectIndexSnapshot,
        projectIndexMatches: Boolean,
    ): ArchitectureSnapshot {
        val androidModules = rootProject.concreteSubprojects()
        val modulePaths = androidModules.map { it.path } + "build-logic"
        val readmes = modulePaths.associateWith { modulePath ->
            val file = if (modulePath == "build-logic") {
                rootProject.rootDir.resolve("build-logic/README.md")
            } else {
                rootProject.project(modulePath).projectDir.resolve("README.md")
            }
            if (!file.isFile) return@associateWith null
            ModuleReadmeIndex(
                sourcePath = relativePath(rootProject.rootDir, file),
                headings = headingRegex.findAll(file.readText()).map { it.groupValues[1].trim() }.toSet(),
            )
        }.filterValues { it != null }.mapValues { it.value!! }

        val sourceMarkers = (androidModules.map { it.projectDir } + rootProject.rootDir.resolve("build-logic"))
            .flatMap { moduleDir ->
                moduleDir.walkSourceFiles().flatMap { source ->
                    buildList {
                        val path = relativePath(rootProject.rootDir, source)
                        add(path)
                        val isTestSource = "/src/test/" in "/$path" || "/src/androidTest/" in "/$path"
                        if (isTestSource && source.readText().contains("addition_isCorrect")) {
                            add("$path#addition_isCorrect")
                        }
                    }
                }
            }

        return ArchitectureSnapshot(
            modulePaths = modulePaths,
            missingBuildFiles = androidModules.filterNot { it.buildFile.isFile }.map { it.path },
            dependencies = androidModules.flatMap { module ->
                collectDeclaredProjectDependencies(module).map { dependency ->
                    ProjectDependencyEdge(module.path, dependency.configuration, dependency.target)
                }
            },
            readmes = readmes,
            resources = index.resources,
            sourceFilePaths = sourceMarkers,
            projectIndexMatches = projectIndexMatches,
            resourcePrefixExceptions = frozenResourcePrefixExceptions,
            standaloneFeature = rootProject.findProperty("standalone")?.toString(),
        )
    }

    private fun collectModule(project: Project): ModuleIndexEntry {
        val readme = project.projectDir.resolve("README.md")
        return ModuleIndexEntry(
            path = project.path,
            kind = moduleKind(project.path),
            namespace = parseNamespace(project.buildFile),
            summary = readSummary(readme),
            readmePath = relativePath(project.rootDir, readme),
            dependencies = collectDeclaredProjectDependencies(project)
                .filterNot { isStandaloneAssemblyDependency(project.path, it) },
            publicEntries = collectPublicEntries(project.rootDir, project.projectDir),
        )
    }

    private fun collectBuildLogic(rootProject: Project): ModuleIndexEntry? {
        val directory = rootProject.rootDir.resolve("build-logic")
        if (!directory.isDirectory) return null
        val readme = directory.resolve("README.md")
        return ModuleIndexEntry(
            path = "build-logic",
            kind = "build-logic",
            namespace = "com.zhangyt.toolkit.buildlogic",
            summary = readSummary(readme),
            readmePath = relativePath(rootProject.rootDir, readme),
            publicEntries = collectPublicEntries(rootProject.rootDir, directory),
        )
    }

    private fun collectPublicEntries(rootDir: File, moduleDir: File): List<PublicEntryIndex> =
        moduleDir.resolve("src/main").walkSourceFiles().flatMap { file ->
            parsePublicDeclarations(file).map { declaration ->
                PublicEntryIndex(declaration, relativePath(rootDir, file))
            }
        }

    private fun collectRoutes(rootProject: Project, project: Project): List<RouteIndexEntry> =
        project.projectDir.resolve("src/main").walkSourceFiles().flatMap { source ->
            if (source.extension == "kt") collectKotlinRoutes(rootProject, project, source) else emptyList()
        }

    private fun collectKotlinRoutes(rootProject: Project, project: Project, source: File): List<RouteIndexEntry> {
        var braceDepth = 0
        val objectScopes = mutableListOf<Pair<String, Int>>()
        return buildList {
            source.forEachLine { rawLine ->
                val line = rawLine.substringBefore("//")
                kotlinObjectRegex.find(line)?.let { objectMatch ->
                    objectScopes += objectMatch.groupValues[1] to braceDepth
                }
                routeRegex.findAll(line).forEach { routeMatch ->
                    val ownerName = objectScopes.joinToString(".") { it.first }
                    val constantName = listOf(ownerName, routeMatch.groupValues[1])
                        .filter(String::isNotBlank)
                        .joinToString(".")
                    add(
                        RouteIndexEntry(
                            owner = project.path,
                            route = routeMatch.groupValues[2],
                            constantName = constantName,
                            sourcePath = relativePath(rootProject.rootDir, source),
                        )
                    )
                }
                braceDepth += line.count { it == '{' } - line.count { it == '}' }
                while (objectScopes.lastOrNull()?.second?.let { braceDepth <= it } == true) {
                    objectScopes.removeAt(objectScopes.lastIndex)
                }
            }
        }
    }

    private fun collectResources(rootProject: Project, project: Project): List<ResourceIndexEntry> {
        val resourceRoots = listOf(
            project.projectDir.resolve("src/main/res"),
            project.projectDir.resolve("src/main/common-res"),
        )
        return resourceRoots.filter(File::isDirectory).flatMap { resourceRoot ->
            resourceRoot.walkTopDown()
                .filter(File::isFile)
                .filterNot { it.name == ".DS_Store" }
                .flatMap { file ->
                    val type = file.parentFile.name.substringBefore('-')
                    if (type == "values" && file.extension == "xml") {
                        valueResourceRegex.findAll(file.readText()).map { match ->
                            ResourceIndexEntry(
                                owner = project.path,
                                type = match.groupValues[1],
                                name = match.groupValues[2],
                                sourcePath = relativePath(rootProject.rootDir, file),
                            )
                        }.asSequence()
                    } else {
                        sequenceOf(
                            ResourceIndexEntry(
                                owner = project.path,
                                type = type,
                                name = file.nameWithoutExtension.substringBefore(".9"),
                                sourcePath = relativePath(rootProject.rootDir, file),
                            )
                        )
                    }
                }
                .toList()
        }
    }

    private fun parseNamespace(buildFile: File): String =
        if (buildFile.isFile) namespaceRegex.find(buildFile.readText())?.groupValues?.get(1) ?: "-" else "-"

    private fun readSummary(readme: File): String {
        if (!readme.isFile) return "README 缺失"
        return readme.readLines()
            .map(String::trim)
            .firstOrNull { it.isNotEmpty() && !it.startsWith('#') && !it.startsWith('>') }
            ?: "README 未提供职责摘要"
    }

    private fun moduleKind(path: String): String = when {
        path == ":app" -> "app"
        path.startsWith(":core:") -> "core"
        path.startsWith(":legacy:") -> "legacy"
        path.endsWith(":api") -> "feature-api"
        path.endsWith(":impl") -> "feature-impl"
        else -> "module"
    }

    private fun collectDeclaredProjectDependencies(project: Project): List<DependencyIndexEntry> =
        project.configurations
            .filter { declaredDependencyConfigurationRegex.containsMatchIn(it.name) }
            .flatMap { configuration ->
                configuration.dependencies.withType(ProjectDependency::class.java).map { dependency ->
                    DependencyIndexEntry(configuration.name, dependency.dependencyProject.path)
                }
            }
            .filterNot { it.target == project.path }
            .distinct()

    private fun isStandaloneAssemblyDependency(from: String, dependency: DependencyIndexEntry): Boolean =
        from == ":module_mine:impl" && dependency.target == ":module_ota:impl"

    private fun parsePublicDeclarations(source: File): List<String> {
        var braceDepth = 0
        return buildList {
            source.forEachLine { rawLine ->
                val line = rawLine.substringBefore("//").trim()
                if (braceDepth == 0 && line.isNotEmpty()) {
                    val declaration = when (source.extension) {
                        "kt" -> kotlinTypeRegex.find(line)?.groupValues?.get(1)
                            ?: kotlinFunctionRegex.find(line)?.groupValues?.get(1)
                            ?: kotlinTypeAliasRegex.find(line)?.groupValues?.get(1)
                        "java" -> javaPublicTypeRegex.find(line)?.groupValues?.get(1)
                        else -> null
                    }
                    declaration?.let(::add)
                }
                braceDepth += line.count { it == '{' } - line.count { it == '}' }
            }
        }.distinct()
    }

    private fun File.walkSourceFiles(): List<File> {
        if (!isDirectory) return emptyList()
        return walkTopDown()
            .onEnter { directory -> directory.name !in setOf("build", ".gradle") }
            .filter(File::isFile)
            .filter { it.extension == "kt" || it.extension == "java" }
            .sortedBy(File::getPath)
            .toList()
    }

    private fun relativePath(rootDir: File, file: File): String =
        file.canonicalFile.relativeTo(rootDir.canonicalFile).invariantSeparatorsPath

    private fun Project.concreteSubprojects(): List<Project> =
        subprojects.filter { it.buildFile.isFile || it.childProjects.isEmpty() }

    private val frozenResourcePrefixExceptions = setOf(
        // Android launcher resources keep platform-standard names.
        "app/src/main/res/drawable/ic_launcher_background.xml",
        "app/src/main/res/drawable-v24/ic_launcher_foreground.xml",
        "app/src/main/res/mipmap-anydpi-v26/ic_launcher.xml",
        "app/src/main/res/mipmap-anydpi-v26/ic_launcher_round.xml",
        "app/src/main/res/mipmap-hdpi/ic_launcher.png",
        "app/src/main/res/mipmap-hdpi/ic_launcher_round.png",
        "app/src/main/res/mipmap-mdpi/ic_launcher.png",
        "app/src/main/res/mipmap-mdpi/ic_launcher_round.png",
        "app/src/main/res/mipmap-xhdpi/ic_launcher.png",
        "app/src/main/res/mipmap-xhdpi/ic_launcher_round.png",
        "app/src/main/res/mipmap-xxhdpi/ic_launcher.png",
        "app/src/main/res/mipmap-xxhdpi/ic_launcher_round.png",
        "app/src/main/res/mipmap-xxxhdpi/ic_launcher.png",
        "app/src/main/res/mipmap-xxxhdpi/ic_launcher_round.png",
        // Existing app template values and manifest contract; new values still require app_*.
        "app/src/main/res/values/colors.xml#black",
        "app/src/main/res/values/colors.xml#purple_200",
        "app/src/main/res/values/colors.xml#purple_500",
        "app/src/main/res/values/colors.xml#purple_700",
        "app/src/main/res/values/colors.xml#teal_200",
        "app/src/main/res/values/colors.xml#teal_700",
        "app/src/main/res/values/colors.xml#white",
        "app/src/main/res/values/themes.xml#Theme.ToolKit",
        "app/src/main/res/xml/network_security_config.xml",
        // Historical design-system style names are public resource API and migrate separately.
        "core/designsystem/src/main/common-res/values/themes.xml#Common_LoadingDialog",
        "core/designsystem/src/main/common-res/values/themes.xml#Common_Theme_Base",
        "core/designsystem/src/main/common-res/values/themes.xml#Common_Theme_Blue",
        "core/designsystem/src/main/common-res/values/themes.xml#Common_Theme_Dark",
        "core/designsystem/src/main/common-res/values/themes.xml#Common_Theme_Green",
        "core/designsystem/src/main/common-res/values/themes.xml#Common_Theme_Purple",
        "core/designsystem/src/main/common-res/values/themes.xml#Common_Theme_Red",
        "core/designsystem/src/main/res/drawable/bg_btn_gray.xml",
        "core/designsystem/src/main/res/drawable/bg_btn_green.xml",
        "core/designsystem/src/main/res/drawable/corner_gray_bg.xml",
        "core/designsystem/src/main/res/drawable/icon_error.png",
        "core/designsystem/src/main/res/layout/dialog_normal.xml",
        "core/designsystem/src/main/res/values/atts.xml#NodeProgressBar",
        "core/designsystem/src/main/res/values/atts.xml#TitleBar",
        "core/designsystem/src/main/res/values/atts.xml#labelTextColor",
        "core/designsystem/src/main/res/values/atts.xml#labelTextSize",
        "core/designsystem/src/main/res/values/atts.xml#max",
        "core/designsystem/src/main/res/values/atts.xml#nodeRadius",
        "core/designsystem/src/main/res/values/atts.xml#primaryNodeColor",
        "core/designsystem/src/main/res/values/atts.xml#progress",
        "core/designsystem/src/main/res/values/atts.xml#progressBarHeight",
        "core/designsystem/src/main/res/values/atts.xml#progressColor",
        "core/designsystem/src/main/res/values/atts.xml#secondaryNodeColor",
        "core/designsystem/src/main/res/values/atts.xml#secondaryNodeRadius",
        "core/designsystem/src/main/res/values/atts.xml#trackColor",
        "core/designsystem/src/main/res/values/atts.xml#widget_title",
        "core/designsystem/src/main/res/values/colors.xml#black",
        "core/designsystem/src/main/res/values/colors.xml#blue",
        "core/designsystem/src/main/res/values/colors.xml#blue_press",
        "core/designsystem/src/main/res/values/colors.xml#gray_bg",
        "core/designsystem/src/main/res/values/colors.xml#gray_btn",
        "core/designsystem/src/main/res/values/colors.xml#gray_btn_press",
        "core/designsystem/src/main/res/values/colors.xml#gray_text",
        "core/designsystem/src/main/res/values/colors.xml#green_unClickable",
        "core/designsystem/src/main/res/values/colors.xml#transparent",
        "core/designsystem/src/main/res/values/colors.xml#white",
    )
}
