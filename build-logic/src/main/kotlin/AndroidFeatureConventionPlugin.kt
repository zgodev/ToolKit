import com.android.build.api.dsl.ApplicationExtension
import com.android.build.api.dsl.LibraryExtension
import org.gradle.api.GradleException
import org.gradle.api.Plugin
import org.gradle.api.Project

class AndroidFeatureConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) = with(target) {
        val supported = setOf("login", "home", "mine", "ota")
        val requested = providers.gradleProperty("standalone").orNull
        if (requested != null && requested !in supported) {
            throw GradleException(
                "Unknown standalone feature '$requested'. Expected one of: ${supported.sorted().joinToString()}"
            )
        }

        val feature = path.split(':')
            .firstOrNull { it.startsWith("module_") }
            ?.removePrefix("module_")
            ?: throw GradleException("toolkit.android.feature must be applied below :module_<name>:impl")
        val standalone = requested == feature

        if (standalone) {
            pluginManager.apply("com.android.application")
            pluginManager.apply("org.jetbrains.kotlin.android")
            extensions.configure(ApplicationExtension::class.java) {
                configureAndroidApplication(this)
                defaultConfig {
                    applicationId = "com.zhangyt.toolkit.$feature"
                    versionCode = 1
                    versionName = "1.0.0"
                    buildConfigField("boolean", "STANDALONE", "true")
                }
                sourceSets.getByName("main") {
                    manifest.srcFile("src/standalone/AndroidManifest.xml")
                    java.srcDir("src/standalone/java")
                }
            }
        } else {
            pluginManager.apply("com.android.library")
            pluginManager.apply("org.jetbrains.kotlin.android")
            extensions.configure(LibraryExtension::class.java) {
                configureAndroidLibrary(this)
                defaultConfig {
                    buildConfigField("boolean", "STANDALONE", "false")
                }
            }
        }

        extensions.extraProperties["toolkitStandalone"] = standalone
    }
}
