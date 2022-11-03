package com.benyhuo.kotlin.deepcopy.compiler

import com.bennyhuo.kotlin.deepcopy.compiler.apt.DeepCopyProcessor
import com.bennyhuo.kotlin.deepcopy.compiler.ksp.DeepCopySymbolProcessorProvider
import com.tschuchort.compiletesting.KotlinCompilation
import com.tschuchort.compiletesting.SourceFile
import com.tschuchort.compiletesting.kspSourcesDir
import com.tschuchort.compiletesting.kspWithCompilation
import com.tschuchort.compiletesting.symbolProcessorProviders
import org.junit.Test
import kotlin.test.assertEquals

/**
 * Created by benny.
 */
class SimpleTest {
    @Test
    fun basicKaptTest() {
        val kotlinSource = SourceFile.kotlin(
            "Point.kt",
            """
            import com.bennyhuo.kotlin.deepcopy.annotations.DeepCopy
            @DeepCopy
            data class Point(var x: Int, var y: Int)
            """
        )
        val compilation = KotlinCompilation().apply {
            sources = listOf(kotlinSource)
            annotationProcessors = listOf(DeepCopyProcessor())
            inheritClassPath = true
            messageOutputStream = System.out
        }

        val result = compilation.compile()

        assertEquals(result.exitCode, KotlinCompilation.ExitCode.OK)

        val generatedFiles = listOf(
            compilation.kaptSourceDir,
            compilation.kaptKotlinGeneratedDir
        ).flatMap { it.walk() }.filter { it.isFile }.associate { it.name to it.readText() }

        assertEquals(
            """
            import kotlin.Int
            import kotlin.jvm.JvmOverloads

            @JvmOverloads
            public fun Point.deepCopy(x: Int = this.x, y: Int = this.y): Point = Point(x, y)
            """.trimIndent().trimEnd(),
            generatedFiles["Point\$\$DeepCopy.kt"]!!.trimEnd()
        )
    }

    @Test
    fun basicKspTest() {
        val kotlinSource = SourceFile.kotlin(
            "Point.kt",
            """
            import com.bennyhuo.kotlin.deepcopy.annotations.DeepCopy
            @DeepCopy
            data class Point(var x: Int, var y: Int)
            """
        )
        val compilation = KotlinCompilation().apply {
            sources = listOf(kotlinSource)
            symbolProcessorProviders = listOf(DeepCopySymbolProcessorProvider())
            inheritClassPath = true
            messageOutputStream = System.out
        }

        val result = compilation.compile()

        assertEquals(result.exitCode, KotlinCompilation.ExitCode.OK)

        val generatedFiles = compilation.kspSourcesDir.walk().filter { it.isFile }.associate {
            it.name to it.readText()
        }

        assertEquals(
            """
            import kotlin.Int
            import kotlin.jvm.JvmOverloads

            @JvmOverloads
            public fun Point.deepCopy(x: Int = this.x, y: Int = this.y): Point = Point(x, y)
            """.trimIndent().trimEnd(),
            generatedFiles["Point\$\$DeepCopy.kt"]!!.trimEnd()
        )
    }

    @Test
    fun runJvmTest() {
        val kotlinSource = SourceFile.kotlin(
            "Point.kt",
            """
            import com.bennyhuo.kotlin.deepcopy.annotations.DeepCopy
            @DeepCopy
            data class Point(var x: Int, var y: Int)
            """
        )

        val mainSource = SourceFile.kotlin(
            "Main.kt",
            """
            fun main() {
                val point = Point(0, 1)
                val deepCopy = point.deepCopy()
                point.x = 1
                point.y = 2
                println(deepCopy)
            }
            """
        )
        val compilation = KotlinCompilation().apply {
            sources = listOf(kotlinSource, mainSource)
            annotationProcessors = listOf(DeepCopyProcessor())
            inheritClassPath = true
            messageOutputStream = System.out
        }

        val result = compilation.compile()

        assertEquals(result.exitCode, KotlinCompilation.ExitCode.OK)

        val entryClass = result.classLoader.loadClass("MainKt")
        val entryFunction = entryClass.getDeclaredMethod("main")
        entryFunction.invoke(null)
    }

    @Test
    fun runJvmForKspTest() {
        val kotlinSource = SourceFile.kotlin(
            "Point.kt",
            """
            import com.bennyhuo.kotlin.deepcopy.annotations.DeepCopy
            @DeepCopy
            data class Point(var x: Int, var y: Int)
            """
        )

        val mainSource = SourceFile.kotlin(
            "Main.kt",
            """
            fun main() {
                val point = Point(0, 1)
                val deepCopy = point.deepCopy(y=3)
                point.x = 1
                point.y = 2
                println(deepCopy)
            }
            """
        )
        val compilation = KotlinCompilation().apply {
            sources = listOf(kotlinSource, mainSource)
            symbolProcessorProviders = listOf(DeepCopySymbolProcessorProvider())
            inheritClassPath = true
            messageOutputStream = System.out
            kspWithCompilation = true
        }

        val result = compilation.compile()

        assertEquals(result.exitCode, KotlinCompilation.ExitCode.OK)

        val entryClass = result.classLoader.loadClass("MainKt")
        val entryFunction = entryClass.getDeclaredMethod("main")
        entryFunction.invoke(null)

        val deepCopyClass = result.classLoader.loadClass("Point__DeepCopyKt")
        println(deepCopyClass.declaredMethods.joinToString { it.toString() })
    }

    @Test
    fun modulesTest() {
        val commonSource = SourceFile.kotlin(
            "Point.kt",
            """
            data class Point(var x: Int, var y: Int)
            """
        )

        val commonCompilation = KotlinCompilation().apply {
            sources = listOf(commonSource)
            symbolProcessorProviders = listOf(DeepCopySymbolProcessorProvider())
            inheritClassPath = true
            messageOutputStream = System.out
            kspWithCompilation = true
        }
        val commonResult = commonCompilation.compile()
        assertEquals(commonResult.exitCode, KotlinCompilation.ExitCode.OK)


        val libASource = SourceFile.kotlin(
            "Config.kt",
            """
            import com.bennyhuo.kotlin.deepcopy.annotations.DeepCopyConfig
            @DeepCopyConfig(values = [Point::class])
            class Config
            """
        )

        val libACompilation = KotlinCompilation().apply {
            sources = listOf(libASource)
            classpaths += commonCompilation.classesDir
            symbolProcessorProviders = listOf(DeepCopySymbolProcessorProvider())
            inheritClassPath = true
            messageOutputStream = System.out
            kspWithCompilation = true
        }

        val libAResult = libACompilation.compile()
        assertEquals(libAResult.exitCode, KotlinCompilation.ExitCode.OK)

        val libAGeneratedFiles = libACompilation.kspSourcesDir.walk().filter { it.isFile }
            .associate { it.name to it.readText() }

        assertEquals(
            """
            import kotlin.Int
            import kotlin.jvm.JvmOverloads
            
            @JvmOverloads
            public fun Point.deepCopy(x: Int = this.x, y: Int = this.y): Point = Point(x, y)
            """.trimIndent().trimEnd(),
            libAGeneratedFiles["Point\$\$DeepCopy.kt"]!!.trimEnd()
        )

        val mainSource = SourceFile.kotlin(
            "Location.kt",
            """
            import com.bennyhuo.kotlin.deepcopy.annotations.DeepCopy
            @DeepCopy
            data class Location(val name: String, val pointE06: Point)
            """
        )
        val mainCompilation = KotlinCompilation().apply {
            sources = listOf(mainSource)
            classpaths += listOf(libACompilation.classesDir, commonCompilation.classesDir)
            symbolProcessorProviders = listOf(DeepCopySymbolProcessorProvider())
            inheritClassPath = true
            messageOutputStream = System.out
            kspWithCompilation = true
        }

        val mainResult = mainCompilation.compile()
        assertEquals(mainResult.exitCode, KotlinCompilation.ExitCode.OK)

        val mainGeneratedFiles = mainCompilation.kspSourcesDir.walk().filter { it.isFile }
            .associate { it.name to it.readText() }
        assertEquals(
            """
            import deepCopy
            import kotlin.String
            import kotlin.jvm.JvmOverloads
            
            @JvmOverloads
            public fun Location.deepCopy(name: String = this.name, pointE06: Point = this.pointE06): Location =
                Location(name, pointE06.deepCopy())
            """.trimIndent().trimEnd(),
            mainGeneratedFiles["Location\$\$DeepCopy.kt"]!!.trimEnd()
        )
    }


}