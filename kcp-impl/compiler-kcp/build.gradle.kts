import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
  kotlin("jvm")
  java
  kotlin("kapt")
  id("com.github.gmazzo.buildconfig")
  id("com.github.johnrengelman.shadow")
}

dependencies {
  compileOnly("org.jetbrains.kotlin:kotlin-stdlib")
  compileOnly("org.jetbrains.kotlin:kotlin-compiler")

  kapt("com.google.auto.service:auto-service:1.0-rc7")
  compileOnly("com.google.auto.service:auto-service-annotations:1.0-rc7")

  testImplementation(project(":annotations"))
  testImplementation(project(":runtime"))
  
  testImplementation(kotlin("test-junit"))
  testImplementation("org.jetbrains.kotlin:kotlin-compiler-embeddable")

  testImplementation("com.bennyhuo.kotlin:kotlin-compile-testing-extensions:0.2")
}

val compileKotlin: KotlinCompile by tasks
compileKotlin.kotlinOptions.freeCompilerArgs += listOf("-Xjvm-default=enable", "-opt-in=kotlin.RequiresOptIn")

buildConfig {
  packageName("$group.kcp")
  buildConfigField("String", "KOTLIN_PLUGIN_ID", "\"${project.properties["KOTLIN_PLUGIN_ID"]}\"")
}

testWithEmbedded()