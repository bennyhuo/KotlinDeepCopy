import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm")
}

dependencies {
    implementation("com.google.devtools.ksp:symbol-processing-api:1.5.31-1.0.1")
    implementation("com.squareup:kotlinpoet:1.10.0")
    implementation("com.squareup:kotlinpoet-ksp:1.10.0")
    
    implementation(project(":annotations"))
}

tasks.withType<KotlinCompile>().configureEach {
    kotlinOptions {
        freeCompilerArgs = listOf(
            "-Xuse-experimental=com.squareup.kotlinpoet.ksp.KotlinPoetKspPreview",
            "-Xuse-experimental=com.google.devtools.ksp.KspExperimental"
        )
    }
}