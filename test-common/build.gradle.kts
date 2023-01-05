repositories {
    mavenCentral()
}
plugins {
    kotlin("jvm")
}

dependencies {
    implementation(kotlin("stdlib"))
    implementation(project(":annotations"))

    testImplementation("com.github.tschuchortdev:kotlin-compile-testing:1.4.9")
    testImplementation("com.github.tschuchortdev:kotlin-compile-testing-ksp:1.4.9")
    testImplementation("com.bennyhuo.kotlin:kotlin-compile-testing-extensions:1.7.10.2-SNAPSHOT")
    
    testImplementation(project(":compiler:compiler-ksp"))
    testImplementation(project(":compiler:compiler-apt"))
    testImplementation(project(":runtime"))

    testImplementation(kotlin("test-common"))
    testImplementation(kotlin("test-annotations-common"))
    testImplementation(kotlin("test-junit"))
}