import org.gradle.api.Plugin
import org.gradle.api.Project

class HiltConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) = with(target) {
        pluginManager.apply("com.google.devtools.ksp")
        pluginManager.apply("dagger.hilt.android.plugin")
        dependencies.add("implementation", library("hilt-android"))
        dependencies.add("ksp", library("hilt-compiler"))
        Unit
    }
}
