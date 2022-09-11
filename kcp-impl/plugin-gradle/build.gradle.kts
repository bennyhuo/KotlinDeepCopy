plugins {
  id("java-gradle-plugin")
  kotlin("jvm")
  id("com.github.gmazzo.buildconfig")
}

dependencies {
  implementation(kotlin("gradle-plugin-api"))
  implementation(kotlin("stdlib"))
}

buildConfig {
  val compilerPluginProject = project(":kcp-impl:compiler-kcp-embeddable")
  packageName("${compilerPluginProject.group}.kcp")
  buildConfigField("String", "KOTLIN_PLUGIN_ID", "\"${property("KOTLIN_PLUGIN_ID")}\"")
  buildConfigField("String", "KOTLIN_PLUGIN_GROUP", "\"${compilerPluginProject.group}\"")
  buildConfigField("String", "KOTLIN_PLUGIN_NAME", "\"${compilerPluginProject.property("POM_ARTIFACT_ID")}\"")
  buildConfigField("String", "KOTLIN_PLUGIN_VERSION", "\"${compilerPluginProject.version}\"")
}

gradlePlugin {
  plugins {
    create("DeepCopyGradlePlugin") {
      id = project.properties["KOTLIN_PLUGIN_ID"] as String
      displayName = "Kotlin DeepCopy plugin for data class"
      description = "Kotlin DeepCopy plugin for data class"
      implementationClass = "com.bennyhuo.kotlin.deepcopy.gradle.DeepCopyGradlePlugin"
    }
  }
}