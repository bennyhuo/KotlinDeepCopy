plugins {
    java
    id("com.github.johnrengelman.shadow")
}

jarWithEmbedded()

dependencies {
    embedded(project(":kcp-impl:compiler-kcp"))
}