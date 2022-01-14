@file:Suppress("unused") // usages in build scripts are not tracked properly

import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import org.gradle.api.Project
import org.gradle.api.artifacts.Configuration
import org.gradle.api.artifacts.ConfigurationContainer
import org.gradle.api.file.DuplicatesStrategy
import org.gradle.api.plugins.JavaPluginExtension
import org.gradle.api.tasks.TaskProvider
import org.gradle.api.tasks.compile.AbstractCompile
import org.gradle.api.tasks.testing.Test
import org.gradle.jvm.tasks.Jar
import org.gradle.kotlin.dsl.getByType
import org.gradle.kotlin.dsl.named
import org.gradle.kotlin.dsl.register
import org.gradle.kotlin.dsl.withType
import java.io.File

const val embedded = "embedded"
const val kotlinEmbeddableRootPackage = "org.jetbrains.kotlin"

val packagesToRelocate =
    listOf(
        "com.intellij",
        "com.google",
        "com.sampullara",
        "org.apache",
        "org.jdom",
        "org.picocontainer",
        "org.jline",
        "org.fusesource",
        "net.jpountz",
        "one.util.streamex",
        "it.unimi.dsi.fastutil",
        "kotlinx.collections.immutable"
    )

// The shaded compiler "dummy" is used to rewrite dependencies in projects that are used with the embeddable compiler
// on the runtime and use some shaded dependencies from the compiler
// To speed-up rewriting process we want to have this dummy as small as possible.
// But due to the shadow plugin bug (https://github.com/johnrengelman/shadow/issues/262) it is not possible to use
// packagesToRelocate list to for the include list. Therefore the exclude list has to be created.
val packagesToExcludeFromDummy =
    listOf(
        "org/jetbrains/kotlin/**",
        "org/intellij/lang/annotations/**",
        "org/jetbrains/jps/**",
        "META-INF/**",
        "com/sun/jna/**",
        "com/thoughtworks/xstream/**",
        "javaslang/**",
        "*.proto",
        "messages/**",
        "net/sf/cglib/**",
        "one/util/streamex/**",
        "org/iq80/snappy/**",
        "org/jline/**",
        "org/xmlpull/**",
        "*.txt"
    )

fun ConfigurationContainer.getOrCreate(name: String): Configuration = findByName(name) ?: create(name)

private fun ShadowJar.configureEmbeddableCompilerRelocation(withJavaxInject: Boolean = true) {
    relocate("com.google.protobuf", "org.jetbrains.kotlin.protobuf")
    packagesToRelocate.forEach {
        relocate(it, "$kotlinEmbeddableRootPackage.$it")
    }
    if (withJavaxInject) {
        relocate("javax.inject", "$kotlinEmbeddableRootPackage.javax.inject")
    }
    relocate("org.fusesource", "$kotlinEmbeddableRootPackage.org.fusesource") {
        // TODO: remove "it." after #KT-12848 get addressed
        exclude("org.fusesource.jansi.internal.CLibrary")
    }
}

private fun Project.compilerShadowJar(taskName: String, body: ShadowJar.() -> Unit): TaskProvider<out ShadowJar> {
    val embeddedConfig = configurations.getOrCreate(embedded)
    val javaPluginExtension = extensions.getByType<JavaPluginExtension>()

    return tasks.register<ShadowJar>(taskName) {
        group = "shadow"
        destinationDirectory.set(project.file(File(buildDir, "libs")))
        duplicatesStrategy = DuplicatesStrategy.EXCLUDE
        from(embeddedConfig)
        from(javaPluginExtension.sourceSets.getByName("main").output)
        body()
    }
}

fun Project.embeddableCompiler(
    taskName: String = "embeddable",
    body: ShadowJar.() -> Unit = {}
): TaskProvider<out ShadowJar> =
    compilerShadowJar(taskName) {
        configureEmbeddableCompilerRelocation()
        body()
    }

fun Project.jarWithEmbedded() {
    val embeddedTask = embeddableCompiler()
    tasks.named<Jar>("jar").configure {
        actions = emptyList()
        dependsOn(embeddedTask)
    }
}

fun Project.testWithEmbedded() {
    configurations.create("embedded").extendsFrom(configurations.getByName("implementation"))
    embeddableCompiler()

    // filter classes dir from compileJava and compileKotlin
    val excludedCompiledFiles = tasks.withType<AbstractCompile>().filter {
        "test" !in it.name.toLowerCase()
    }.map {
        it.destinationDirectory.get().asFile
    }

    tasks.named<Test>("test") {
        dependsOn(tasks.getByName("embeddable"))
        this.classpath += tasks.getByName("embeddable").outputs.files
        this.classpath = this.classpath.filter { it !in excludedCompiledFiles }
    }
}