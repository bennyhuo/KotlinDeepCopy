package com.benyhuo.kotlin.deepcopy.compiler

import com.bennyhuo.kotlin.deepcopy.compiler.apt.DeepCopyProcessor
import com.bennyhuo.kotlin.deepcopy.compiler.ksp.DeepCopySymbolProcessorProvider
import com.tschuchort.compiletesting.KotlinCompilation
import com.tschuchort.compiletesting.SourceFile
import com.tschuchort.compiletesting.symbolProcessorProviders
import java.io.File
import kotlin.test.assertEquals

/**
 * Created by benny.
 */
const val SOURCE_START_LINE = "// SOURCE"
const val GENERATED_START_LINE = "// GENERATED"
val FILE_NAME_PATTERN = Regex("""// ((\w+)\.(\w+))\s*""")

class SourceFileInfo(val name: String) {
    val sourceBuilder = StringBuilder()

    override fun toString(): String {
        return "$name: \n$sourceBuilder"
    }
}

fun doTest(path: String, compilation: KotlinCompilation, outputDir: KotlinCompilation.() -> File) {
    val lines = File(path).readLines()
        .dropWhile { it.trim() != SOURCE_START_LINE }
    val sourceLines =
        lines.takeWhile { it.trim() != GENERATED_START_LINE }.drop(1)
    val generatedLines =
        lines.dropWhile { it.trim() != GENERATED_START_LINE }.drop(1)

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

    compilation.sources = sourceFiles
    assertEquals(compilation.compile().exitCode, KotlinCompilation.ExitCode.OK)

    val generatedSource = compilation.outputDir().walkTopDown()
        .filter { !it.isDirectory }
        .fold(StringBuilder()) { acc, it ->
            acc.append("//-------${it.name}------\n")
            acc.append(it.readText())
            acc
        }.toString()

    assertEquals(expectGenerateSource, generatedSource)
}

fun compilationWithKsp(): KotlinCompilation {
    return KotlinCompilation().apply {
        inheritClassPath = true
        symbolProcessorProviders = listOf(DeepCopySymbolProcessorProvider())
    }
}

fun compilationWithKapt(): KotlinCompilation {
    return KotlinCompilation().apply {
        inheritClassPath = true
        annotationProcessors = listOf(DeepCopyProcessor())
    }
}
