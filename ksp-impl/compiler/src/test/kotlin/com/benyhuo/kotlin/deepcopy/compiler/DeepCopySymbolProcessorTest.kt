package com.benyhuo.kotlin.deepcopy.compiler

import com.bennyhuo.kotlin.deepcopy.compiler.DeepCopySymbolProcessorProvider
import com.tschuchort.compiletesting.KotlinCompilation
import com.tschuchort.compiletesting.SourceFile
import com.tschuchort.compiletesting.kspSourcesDir
import com.tschuchort.compiletesting.symbolProcessorProviders
import org.junit.Test
import java.io.File
import kotlin.test.assertEquals

/**
 * Created by benny at 2021/6/21 7:00.
 */
class DeepCopySymbolProcessorTest {
    @Test
    fun testBasic() {
        val annotation = SourceFile.java(
            "DeepCopy.java", """
            package com.bennyhuo.kotlin.deepcopy.annotations;

            public @interface DeepCopy {
            }
        """.trimIndent()
        )
        val kotlinSource = SourceFile.fromPath(File("src/test/resources/Basic.kt"))

        val compilation = KotlinCompilation().apply {
            sources = listOf(annotation, kotlinSource)
            symbolProcessorProviders = listOf(DeepCopySymbolProcessorProvider())
        }

        assertEquals(compilation.compile().exitCode, KotlinCompilation.ExitCode.OK)

        compilation.kspSourcesDir.walkTopDown()
            .filter { !it.isDirectory }
            .forEach {
                println("-------${it.name}------")
                println(it.readText())
            }
    }
}