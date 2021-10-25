import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
  kotlin("jvm")
  kotlin("kapt")
  id("com.github.gmazzo.buildconfig")
}

dependencies {
  compileOnly("org.jetbrains.kotlin:kotlin-compiler-embeddable")

  kapt("com.google.auto.service:auto-service:1.0-rc7")
  compileOnly("com.google.auto.service:auto-service-annotations:1.0-rc7")

  testImplementation(kotlin("test-junit"))
  testImplementation("org.jetbrains.kotlin:kotlin-compiler-embeddable")
  testImplementation("com.github.tschuchortdev:kotlin-compile-testing:1.4.5")
}

val compileKotlin: KotlinCompile by tasks
compileKotlin.kotlinOptions.freeCompilerArgs += "-Xjvm-default=enable"


buildConfig {
  packageName(group.toString())
  buildConfigField("String", "KOTLIN_PLUGIN_ID", "\"${project.properties["KOTLIN_PLUGIN_ID"]}\"")
}
