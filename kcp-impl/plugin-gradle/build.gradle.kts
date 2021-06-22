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
  val project = project(":kcp-impl:plugin-compiler")
  packageName(project.group.toString())
  buildConfigField("String", "KOTLIN_PLUGIN_ID", "\"${project.properties["KOTLIN_PLUGIN_ID"]}\"")
  buildConfigField("String", "KOTLIN_PLUGIN_GROUP", "\"${project.group}\"")
  buildConfigField("String", "KOTLIN_PLUGIN_NAME", "\"${project.name}\"")
  buildConfigField("String", "KOTLIN_PLUGIN_VERSION", "\"${project.version}\"")
}

gradlePlugin {
  plugins {
    create("DeepCopyGradlePlugin") {
      id = project.properties["KOTLIN_PLUGIN_ID"] as String
      displayName = "Kotlin DeepCopy plugin for data class"
      description = "Kotlin DeepCopy plugin for data class"
      implementationClass = "com.bennyhuo.kotlin.kcp.deepcopy.gradle.DeepCopyGradlePlugin"
    }
  }
}