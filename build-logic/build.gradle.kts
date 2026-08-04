plugins {
    `kotlin-dsl`
}

dependencyLocking {
    lockAllConfigurations()
}

group = "com.zhangyt.toolkit.buildlogic"

repositories {
    google()
    mavenCentral()
    gradlePluginPortal()
}

dependencies {
    implementation(libs.android.gradle.plugin)
    implementation(libs.kotlin.gradle.plugin)
    implementation(libs.ksp.gradle.plugin)
    implementation(libs.hilt.gradle.plugin)
    testImplementation(gradleTestKit())
    testImplementation(libs.junit4)
}

gradlePlugin {
    plugins {
        register("androidApplication") {
            id = "toolkit.android.application"
            implementationClass = "AndroidApplicationConventionPlugin"
        }
        register("androidLibrary") {
            id = "toolkit.android.library"
            implementationClass = "AndroidLibraryConventionPlugin"
        }
        register("androidFeature") {
            id = "toolkit.android.feature"
            implementationClass = "AndroidFeatureConventionPlugin"
        }
        register("kotlinLibrary") {
            id = "toolkit.kotlin.library"
            implementationClass = "KotlinLibraryConventionPlugin"
        }
        register("hilt") {
            id = "toolkit.android.hilt"
            implementationClass = "HiltConventionPlugin"
        }
        register("arouter") {
            id = "toolkit.android.arouter"
            implementationClass = "ARouterConventionPlugin"
        }
        register("architectureGuard") {
            id = "toolkit.architecture-guard"
            implementationClass = "ArchitectureGuardPlugin"
        }
    }
}
