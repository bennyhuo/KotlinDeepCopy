import org.jetbrains.intellij.tasks.PublishPluginTask

plugins {
    kotlin("jvm")
    id("org.jetbrains.intellij") version("1.9.0")
}

dependencies {
    implementation(project(":kcp-impl:compiler-kcp"))
}

intellij {
    version.set("2022.2.1")
    plugins.set(listOf("Kotlin", "com.intellij.gradle"))
    pluginName.set("DeepCopy")
    updateSinceUntilBuild.set(false)
}

tasks {
    withType<PublishPluginTask> {
        project.property("intellij.token")?.let {
            token.set(it.toString())
        }
    }
}