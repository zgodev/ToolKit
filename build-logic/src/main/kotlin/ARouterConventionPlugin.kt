import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.tasks.compile.JavaCompile
import org.jetbrains.kotlin.gradle.plugin.KaptExtension

class ARouterConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) = with(target) {
        val moduleName = path.trim(':').replace(':', '_')
        pluginManager.apply("org.jetbrains.kotlin.kapt")
        dependencies.add("implementation", library("arouter-api"))
        dependencies.add("kapt", library("arouter-compiler"))

        extensions.configure(KaptExtension::class.java) {
            arguments {
                arg("AROUTER_MODULE_NAME", moduleName)
            }
        }
        tasks.withType(JavaCompile::class.java).configureEach {
            options.compilerArgs.add("-AAROUTER_MODULE_NAME=$moduleName")
        }
    }
}
