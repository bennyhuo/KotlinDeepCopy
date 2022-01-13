plugins {
    kotlin("jvm")
    id("org.jetbrains.intellij") version("1.1.2")
}

dependencies {
    implementation(project(":kcp-impl:compiler-kcp"))
}

intellij {
    version.set("2021.3.1")
    plugins.set(listOf("Kotlin", "com.intellij.gradle"))
    pluginName.set("DeepCopy")
    updateSinceUntilBuild.set(false)
//    alternativeIdePath props["AndroidStudio.path"]
//    alternativeIdePath props["intellijIU.path"]

}