plugins {
    java
    id("com.bennyhuo.kotlin.plugin.embeddable")
}

dependencies {
    embedded(project(":kcp-impl:compiler-kcp"))
}