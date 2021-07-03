plugins {
    kotlin("jvm")
    id("org.jetbrains.intellij") version("1.1.2")
}

dependencies {
    implementation(project(":kcp-impl:plugin-compiler"))
}

intellij {
    localPath.set("C:\\Users\\benny\\AppData\\Local\\JetBrains\\Toolbox\\apps\\IDEA-U\\ch-0\\211.7142.45")
    plugins.set(listOf("Kotlin"))
    pluginName.set("DeepCopy")
    updateSinceUntilBuild.set(false)
//    alternativeIdePath props["AndroidStudio.path"]
//    alternativeIdePath props["intellijIU.path"]

}