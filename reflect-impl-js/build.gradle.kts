plugins {
    id("org.jetbrains.kotlin.js")
}

dependencies {
    implementation(kotlin("stdlib-js"))

    testImplementation(kotlin("test-js"))
}

kotlin {
    js(IR) {
        moduleName = "deepcopy-reflect-js"
        binaries.library()
        nodejs()
    }
}
