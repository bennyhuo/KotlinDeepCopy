plugins {
    kotlin("jvm")
}

dependencies {
    implementation("com.google.devtools.ksp:symbol-processing-api:1.5.31-1.0.0")
    implementation("com.squareup:kotlinpoet:1.2.0")
    implementation(project(":annotations"))

    testImplementation("com.github.tschuchortdev:kotlin-compile-testing:1.4.5")
    testImplementation("com.github.tschuchortdev:kotlin-compile-testing-ksp:1.4.5")

    testImplementation(kotlin("test-common"))
    testImplementation(kotlin("test-annotations-common"))
    testImplementation(kotlin("test-junit"))
}