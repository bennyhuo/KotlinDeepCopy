plugins {
    kotlin("jvm")
}

dependencies {
    implementation(kotlin("stdlib"))
    implementation(project(":annotations"))

    testImplementation("com.github.tschuchortdev:kotlin-compile-testing:1.4.5")
    testImplementation("com.github.tschuchortdev:kotlin-compile-testing-ksp:1.4.5")
    
    testImplementation(project(":compiler:compiler-ksp"))
    testImplementation(project(":compiler:compiler-apt"))
    
    testImplementation(kotlin("test-common"))
    testImplementation(kotlin("test-annotations-common"))
    testImplementation(kotlin("test-junit"))
}