plugins {
    java
    id("com.github.johnrengelman.shadow")
}

dependencies {
    runtimeOnly(project(":kcp-impl:compiler-kcp"))
}

jarWithEmbedded()