package com.bennyhuo.kotlin.kcp.deepcopy.compiler

import com.bennyhuo.kotlin.deepcopy.compiler.kcp.DeepCopyComponentRegistrar
import com.tschuchort.compiletesting.KotlinCompilation
import com.tschuchort.compiletesting.SourceFile
import org.junit.Test
import java.io.ByteArrayOutputStream
import java.io.PrintStream
import kotlin.test.assertEquals

/**
 * Created by benny.
 */
class SimpleTest {

    @Test
    fun irTest() {
        val kotlinSource = SourceFile.kotlin(
            "Point.kt",
            """
            import com.bennyhuo.kotlin.deepcopy.annotations.DeepCopy
            @DeepCopy
            data class Point(var x: Int, var y: Int)
            """
        )

        val irDumpComponentRegistrar = IrDumpComponentRegistrar()
        val compilation = KotlinCompilation().apply {
            sources = listOf(kotlinSource)
            compilerPlugins = listOf(DeepCopyComponentRegistrar(), irDumpComponentRegistrar)
            inheritClassPath = true
            messageOutputStream = System.out
        }

        val result = compilation.compile()

        assertEquals(result.exitCode, KotlinCompilation.ExitCode.OK)

        println(irDumpComponentRegistrar.irDumpExtension.rawIr)
        println(irDumpComponentRegistrar.irDumpExtension.kotlinLikeIr)
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
            import com.bennyhuo.kotlin.deepcopy.DeepCopyable
            
            fun main() {
                val point = Point(0, 1)
                val deepCopy = point.deepCopy()
                point.x = 1
                point.y = 2
                println(deepCopy)
                println(point is DeepCopyable<*>)
            }
            """
        )
        val compilation = KotlinCompilation().apply {
            sources = listOf(kotlinSource, mainSource)
            compilerPlugins = listOf(DeepCopyComponentRegistrar())
            inheritClassPath = true
            messageOutputStream = System.out
        }

        val result = compilation.compile()

        assertEquals(result.exitCode, KotlinCompilation.ExitCode.OK)

        assertEquals(
            """
            Point(x=0, y=1)
            true
            """.trimIndent(),
            captureStdOut {
                val entryClass = result.classLoader.loadClass("MainKt")
                val entryFunction = entryClass.getDeclaredMethod("main")
                entryFunction.invoke(null)
            }
        )
    }


    private fun captureStdOut(block: () -> Unit): String {
        val originalStdOut = System.out
        val originalStdErr = System.err
        val stdOutStream = ByteArrayOutputStream()
        val printStream = PrintStream(stdOutStream)
        System.setOut(printStream)
        System.setErr(printStream)
        try {
            block()
        } finally {
            System.setOut(originalStdOut)
            System.setErr(originalStdErr)
        }
        return stdOutStream.toString().unify()
    }

    private fun String.unify() = replace("\r\n", "\n").trimEnd()

}