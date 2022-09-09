plugins {
    kotlin("multiplatform")
    id("com.google.devtools.ksp") version kspVersion
}

version = "unspecified"

repositories {
    mavenCentral()
}

kotlin {
    js(IR) {
        nodejs {
        }
        binaries.executable()
    }

    sourceSets {
        val jsMain by getting {
            kotlin.srcDir("build/generated/ksp/js/jsMain/kotlin")

            dependencies {
                implementation(kotlin("stdlib-js"))
                implementation(project(":annotations"))
            }
        }
    }
}

dependencies {
    "kspJs"(project(":compiler:compiler-ksp"))
}