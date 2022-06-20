plugins {
    `kotlin-dsl`
}

repositories {
    maven("https://mirrors.tencent.com/nexus/repository/maven-public")
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-gradle-plugin:1.6.21")
    implementation("com.github.jengelman.gradle.plugins:shadow:6.1.0")
}