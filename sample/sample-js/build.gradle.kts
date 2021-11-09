plugins {
    id("org.jetbrains.kotlin.js")
    id("com.google.devtools.ksp") version "1.5.31-1.0.0"
}

version = "unspecified"

repositories {
    mavenCentral()
}

dependencies {
    implementation(kotlin("stdlib-js"))
    implementation(project(":annotations"))
    ksp(project(":compiler:compiler-ksp"))
}

kotlin {
    sourceSets {
        val main by getting {
            kotlin.srcDir("build/generated/ksp/main/kotlin")
        }
    }
}

kotlin {
    js {
        nodejs {
        }
        binaries.executable()
    }
}