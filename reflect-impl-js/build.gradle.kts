plugins {
    id("org.jetbrains.kotlin.js")
}

group = "com.bennyhuo.kotlin.hello"
version = "1.0-SNAPSHOT"

dependencies {
    implementation(kotlin("stdlib-js"))
}

kotlin {
    js(IR) {
        moduleName = "deepcopy-reflect-js"

        binaries.executable()
        binaries.library()
        nodejs {

        }
    }
}