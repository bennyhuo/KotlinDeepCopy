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
        val FILE_NAME_PATTERN = Regex("""// ((\w+)\.(\w+))\s*""")
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

    @Test
    fun testCollections() {
        doTest("testData/Collections.kt")
    }

    class SourceFileInfo(val name: String) {
        val sourceBuilder = StringBuilder()

        override fun toString(): String {
            return "$name: \n$sourceBuilder"
        }
    }

    private fun doTest(path: String) {
        val lines = File(path).readLines().dropWhile { it.trim() != SOURCE_START_LINE }
        val sourceLines = lines.takeWhile { it.trim() != GENERATED_START_LINE }.drop(1)
        val generatedLines = lines.dropWhile { it.trim() != GENERATED_START_LINE }.drop(1)

        val sourceFileLines = ArrayList<SourceFileInfo>()
        sourceFileLines.add(SourceFileInfo("default_file.kt"))
        sourceLines.fold(sourceFileLines) { acc, line ->
            val result = FILE_NAME_PATTERN.find(line)
            if (result == null) {
                acc.last().sourceBuilder.append(line).appendLine()
            } else {
                acc.add(SourceFileInfo(result.groupValues[1]))
            }
            acc
        }

        val sourceFiles =
            sourceFileLines.map { SourceFile.new(it.name, it.sourceBuilder.toString()) }

        val expectGenerateSource = generatedLines.joinToString("\n")

        val compilation = KotlinCompilation().apply {
            inheritClassPath = true
            sources = sourceFiles
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