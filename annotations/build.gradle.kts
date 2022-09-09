import org.jetbrains.kotlin.gradle.targets.js.nodejs.NodeJsRootExtension
import org.jetbrains.kotlin.gradle.targets.js.nodejs.NodeJsRootPlugin

plugins {
    kotlin("multiplatform")
}

kotlin {
    jvm()
    js(IR) {
        nodejs {}
    }
    sourceSets {
        val commonMain by getting {
            dependencies {
                api("com.bennyhuo.kotlin:annotations-module-support:0.1")
            }
        }
    }
}

rootProject.plugins.withType<NodeJsRootPlugin> {
    rootProject.the<NodeJsRootExtension>().nodeVersion = "16.0.0"
}