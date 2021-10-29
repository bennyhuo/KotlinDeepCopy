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

    companion object {
        const val SOURCE_START_LINE = "// SOURCE"
        const val GENERATED_START_LINE = "// GENERATED"
    }

    @Test
    fun testBasic() {
        doTest("testData/Basic.kt")
    }

    @Test
    fun testGenerics() {
        doTest("testData/Generics.kt")
    }

    @Test
    fun testTypeAliases() {
        doTest("testData/TypeAliases.kt")
    }

    @Test
    fun testNullables() {
        doTest("testData/Nullables.kt")
    }

    @Test
    fun testInnerClasses() {
        doTest("testData/InnerClasses.kt")
    }
    
    @Test
    fun testConfig() {
        doTest("testData/Config.kt")
    }

    @Test
    fun testRecursive() {
        doTest("testData/Recursive.kt")
    }

    private fun doTest(path: String) {
        val lines = File(path).readLines().dropWhile { it.trim() != SOURCE_START_LINE }
        val sourceLines = lines.takeWhile { it.trim() != GENERATED_START_LINE }.drop(1)
        val generatedLines = lines.dropWhile { it.trim() != GENERATED_START_LINE }.drop(1)

        val kotlinSource = SourceFile.kotlin("test.kt", sourceLines.joinToString("\n"))
        val expectGenerateSource = generatedLines.joinToString("\n")

        val compilation = KotlinCompilation().apply {
            inheritClassPath = true
            sources = listOf(kotlinSource)
            symbolProcessorProviders = listOf(DeepCopySymbolProcessorProvider())
        }

        assertEquals(compilation.compile().exitCode, KotlinCompilation.ExitCode.OK)

        val generatedSource = compilation.kspSourcesDir.walkTopDown()
            .filter { !it.isDirectory }
            .fold(StringBuilder()) { acc, it ->
                acc.append("//-------${it.name}------\n")
                acc.append(it.readText())
                acc
            }.toString()

        assertEquals(expectGenerateSource, generatedSource)
    }
}