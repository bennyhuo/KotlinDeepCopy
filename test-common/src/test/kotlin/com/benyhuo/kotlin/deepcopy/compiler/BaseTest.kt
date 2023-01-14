package com.benyhuo.kotlin.deepcopy.compiler

import com.bennyhuo.kotlin.compiletesting.extensions.module.KotlinModule
import com.bennyhuo.kotlin.compiletesting.extensions.module.checkResult
import com.bennyhuo.kotlin.compiletesting.extensions.source.FileBasedModuleInfoLoader
import com.bennyhuo.kotlin.compiletesting.extensions.source.SourceModuleInfo
import java.io.File
import org.jetbrains.kotlin.util.capitalizeDecapitalize.capitalizeAsciiOnly

/**
 * Created by benny.
 */
abstract class BaseTest {

    private val testCaseDirCommon = File("testData")

    abstract val testCaseDir: File

    private val extensions = arrayOf("", ".kt", ".txt")

    fun doTest(name: String? = null) {
        val caseName = name ?: retrieveTestCaseName()
        val testCaseFile = listOf(testCaseDir, testCaseDirCommon).firstNotNullOf { findTestCaseFile(it, caseName) }

        val loader = FileBasedModuleInfoLoader(testCaseFile.path)
        loader.loadSourceModuleInfos().map(::createKotlinModule)
            .checkResult(
                loader.loadExpectModuleInfos(),
                checkExitCode = false,
                checkGeneratedFiles = true,
                checkCompilerOutput = true
            )
    }

    private fun retrieveTestCaseName(): String {
        return Throwable().stackTrace.first {
            it.className != BaseTest::class.java.name
        }.methodName
    }

    private fun findTestCaseFile(parentDir: File, name: String): File? {
        return extensions.firstNotNullOfOrNull { extension ->
            File(parentDir, "$name$extension").takeIf {
                it.exists()
            } ?: File(
                parentDir, "${name.capitalizeAsciiOnly()}$extension"
            ).takeIf {
                it.exists()
            }
        }
    }

    abstract fun createKotlinModule(moduleInfo: SourceModuleInfo): KotlinModule

}