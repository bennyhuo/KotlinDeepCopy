package com.bennyhuo.kotlin.kcp.deepcopy.compiler

import com.bennyhuo.kotlin.compiletesting.extensions.module.KotlinModule
import com.bennyhuo.kotlin.compiletesting.extensions.module.compileAll
import com.bennyhuo.kotlin.compiletesting.extensions.module.resolveAllDependencies
import com.bennyhuo.kotlin.compiletesting.extensions.result.ResultCollector
import com.bennyhuo.kotlin.compiletesting.extensions.source.SingleFileModuleInfoLoader
import com.bennyhuo.kotlin.deepcopy.compiler.kcp.DeepCopyComponentRegistrar
import org.junit.Test

class DeepCopyTest {
    @Test
    fun basic() {
        testBase("basic.kt")
    }

    @Test
    fun declaredDeepCopy() {
        testBase("declaredDeepCopy.kt")
    }

    @Test
    fun deepCopyForInterface() {
        testBase("deepCopyForInterface.kt")
    }

    @Test
    fun collectionElementCheck() {
        testBase("collectionElementCheck.kt")
    }

    @Test
    fun modules() {
        testBase("modules.kt")
    }

    private fun testBase(fileName: String) {
        val loader = SingleFileModuleInfoLoader("testData/$fileName")
        val sourceModuleInfos = loader.loadSourceModuleInfos()

        val modules = sourceModuleInfos.map {
            KotlinModule(it, componentRegistrars = listOf(DeepCopyComponentRegistrar()))
        }

        modules.resolveAllDependencies()
        modules.compileAll()

        val resultMap = modules.associate {
            it.name to it.runJvm()
        }

        loader.loadExpectModuleInfos().fold(ResultCollector()) { collector, expectModuleInfo ->
            collector.collectModule(expectModuleInfo.name)
            expectModuleInfo.sourceFileInfos.forEach {
                collector.collectFile(it.fileName)
                collector.collectLine(it.sourceBuilder, resultMap[expectModuleInfo.name]?.get(it.fileName))
            }
            collector
        }.apply()
    }
}