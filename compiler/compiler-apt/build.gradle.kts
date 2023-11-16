import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    java
    kotlin("jvm")
}

dependencies {
    api("org.jetbrains.kotlin:kotlin-stdlib:$kotlinVersion")
    api("org.jetbrains.kotlinx:kotlinx-metadata-jvm:0.7.0")
    api("com.bennyhuo:aptutils:1.8")
    api("com.squareup:kotlinpoet:1.10.2")
    api("com.squareup:kotlinpoet-metadata:1.10.2")

    api(project(":annotations"))

    api("com.bennyhuo.kotlin:apt-module-support:$moduleSupportVersion")
}

tasks.withType<KotlinCompile>().configureEach {
    kotlinOptions {
        freeCompilerArgs = listOf(
            "-Xuse-experimental=com.squareup.kotlinpoet.metadata.KotlinPoetMetadataPreview",
            "-Xopt-in=kotlin.contracts.ExperimentalContracts"
        )
    }
}
