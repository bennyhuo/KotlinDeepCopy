plugins {
    kotlin("jvm")
    id("com.bnorm.power.kotlin-power-assert") version "0.9.0"
}

configure<com.bnorm.power.PowerAssertGradleExtension> {
    functions = listOf(
        "kotlin.assert",
        "kotlin.test.assertTrue",
        "kotlin.test.assertEquals"
    )
}

dependencies {
    implementation("com.google.devtools.ksp:symbol-processing-api:1.5.10-1.0.0-beta02")
    implementation("com.squareup:kotlinpoet:1.2.0")

    testImplementation("com.github.tschuchortdev:kotlin-compile-testing-ksp:1.4.2")

    testImplementation(kotlin("test-common"))
    testImplementation(kotlin("test-annotations-common"))
    testImplementation(kotlin("test-junit"))
}