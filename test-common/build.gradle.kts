repositories {
    mavenCentral()
}
plugins {
    kotlin("jvm")
}

dependencies {
    implementation(kotlin("stdlib"))
    implementation(project(":annotations"))

    testImplementation("com.bennyhuo.kotlin:kotlin-compile-testing-extensions:$compileTestingExtensionsVersion")

    testImplementation(project(":compiler:compiler-ksp"))
    testImplementation(project(":compiler:compiler-apt"))
    testImplementation(project(":runtime"))

    testImplementation(kotlin("test-common"))
    testImplementation(kotlin("test-annotations-common"))
    testImplementation(kotlin("test-junit"))
}
