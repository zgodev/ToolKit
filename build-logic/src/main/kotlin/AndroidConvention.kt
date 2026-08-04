import com.android.build.api.dsl.ApplicationExtension
import com.android.build.api.dsl.LibraryExtension
import org.gradle.api.JavaVersion
import org.gradle.api.Project
import org.gradle.api.artifacts.VersionCatalogsExtension
import org.gradle.api.plugins.JavaPluginExtension
import org.gradle.api.tasks.testing.Test
import org.gradle.jvm.toolchain.JavaLanguageVersion
import org.jetbrains.kotlin.gradle.dsl.KotlinJvmProjectExtension
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

internal const val COMPILE_SDK = 34
internal const val MIN_SDK = 21
internal const val TARGET_SDK = 34

internal fun Project.configureAndroidApplication(extension: ApplicationExtension) {
    extension.apply {
        compileSdk = COMPILE_SDK
        defaultConfig {
            minSdk = MIN_SDK
            targetSdk = TARGET_SDK
            testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        }
        compileOptions {
            sourceCompatibility = JavaVersion.VERSION_17
            targetCompatibility = JavaVersion.VERSION_17
        }
        buildFeatures {
            viewBinding = true
            buildConfig = true
        }
        packaging.resources.excludes += setOf(
            "META-INF/DEPENDENCIES",
            "META-INF/NOTICE",
            "META-INF/LICENSE",
        )
        bundle.language.enableSplit = false
        lint.baseline = file("lint-baseline.xml")
    }
    configureKotlin17()
    addAndroidTestDependencies()
}

internal fun Project.configureAndroidLibrary(extension: LibraryExtension) {
    val consumerRules = file("consumer-rules.pro")
    extension.apply {
        compileSdk = COMPILE_SDK
        defaultConfig {
            minSdk = MIN_SDK
            testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
            if (consumerRules.exists()) {
                consumerProguardFiles(consumerRules)
            }
        }
        compileOptions {
            sourceCompatibility = JavaVersion.VERSION_17
            targetCompatibility = JavaVersion.VERSION_17
        }
        buildFeatures {
            viewBinding = true
            buildConfig = true
        }
        lint.baseline = file("lint-baseline.xml")
    }
    configureKotlin17()
    addAndroidTestDependencies()
}

internal fun Project.configureKotlin17() {
    tasks.withType(KotlinCompile::class.java).configureEach {
        kotlinOptions.jvmTarget = "17"
    }
}

internal fun Project.configureJvmLibrary() {
    extensions.configure(JavaPluginExtension::class.java) {
        toolchain.languageVersion.set(JavaLanguageVersion.of(17))
    }
    extensions.configure(KotlinJvmProjectExtension::class.java) {
        jvmToolchain(17)
    }
    tasks.withType(Test::class.java).configureEach {
        useJUnit()
    }
    dependencies.add("testImplementation", library("junit4"))
}

internal fun Project.addAndroidTestDependencies() {
    dependencies.add("testImplementation", library("junit4"))
    dependencies.add("androidTestImplementation", library("androidx-test-junit"))
    dependencies.add("androidTestImplementation", library("androidx-test-espresso"))
}

internal fun Project.addComposeDependencies() {
    dependencies.add("implementation", dependencies.platform(library("androidx-compose-bom")))
    dependencies.add("implementation", library("androidx-compose-ui"))
    dependencies.add("implementation", library("androidx-compose-foundation"))
    dependencies.add("implementation", library("androidx-compose-material3"))
    dependencies.add("implementation", library("androidx-compose-material-icons-extended"))
    dependencies.add("implementation", library("androidx-compose-ui-tooling-preview"))
    dependencies.add("implementation", library("androidx-activity-compose"))
    dependencies.add("implementation", library("androidx-lifecycle-runtime-compose"))
    dependencies.add("debugImplementation", library("androidx-compose-ui-tooling"))
    dependencies.add("debugImplementation", library("androidx-compose-ui-test-manifest"))
    dependencies.add("androidTestImplementation", dependencies.platform(library("androidx-compose-bom")))
    dependencies.add("androidTestImplementation", library("androidx-compose-ui-test-junit4"))
}

internal fun Project.configureAndroidCompose(extension: ApplicationExtension) {
    extension.buildFeatures.compose = true
    extension.composeOptions.kotlinCompilerExtensionVersion = version("composeCompiler")
}

internal fun Project.configureAndroidCompose(extension: LibraryExtension) {
    extension.buildFeatures.compose = true
    extension.composeOptions.kotlinCompilerExtensionVersion = version("composeCompiler")
}

internal fun Project.library(alias: String) =
    extensions.getByType(VersionCatalogsExtension::class.java)
        .named("libs")
        .findLibrary(alias)
        .get()

internal fun Project.version(alias: String): String =
    extensions.getByType(VersionCatalogsExtension::class.java)
        .named("libs")
        .findVersion(alias)
        .get()
        .requiredVersion
