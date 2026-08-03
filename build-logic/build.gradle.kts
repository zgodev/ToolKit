plugins {
    `kotlin-dsl`
}

group = "com.zhangyt.toolkit.buildlogic"

repositories {
    google()
    mavenCentral()
    gradlePluginPortal()
}

dependencies {
    implementation("com.android.tools.build:gradle:8.2.2")
    implementation("org.jetbrains.kotlin:kotlin-gradle-plugin:1.9.22")
    implementation("com.google.devtools.ksp:com.google.devtools.ksp.gradle.plugin:1.9.22-1.0.17")
    implementation("com.google.dagger:hilt-android-gradle-plugin:2.51.1")
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
    }
}
