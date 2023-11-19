import org.jetbrains.intellij.tasks.PublishPluginTask

plugins {
    kotlin("jvm")
    id("org.jetbrains.intellij") version("1.16.0")
}

dependencies {
    implementation(project(":kcp-impl:compiler-kcp"))
}

intellij {
    version.set("2023.2.5")
    plugins.set(listOf("org.jetbrains.kotlin:232-1.9.20-release-507-IJ10072.27", "com.intellij.gradle:232.10227.11"))
    pluginName.set("DeepCopy")
    updateSinceUntilBuild.set(false)
}

tasks {
    withType<PublishPluginTask> {
        project.findProperty("intellij.token")?.let {
            token.set(it.toString())
        }
    }
}
