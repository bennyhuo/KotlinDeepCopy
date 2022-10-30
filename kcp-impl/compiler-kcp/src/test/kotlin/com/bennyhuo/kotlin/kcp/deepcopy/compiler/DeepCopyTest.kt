package com.bennyhuo.kotlin.kcp.deepcopy.compiler

import com.bennyhuo.kotlin.compiletesting.extensions.module.KotlinModule
import com.bennyhuo.kotlin.compiletesting.extensions.module.checkResult
import com.bennyhuo.kotlin.compiletesting.extensions.source.FileBasedModuleInfoLoader
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
        val loader = FileBasedModuleInfoLoader("testData/$fileName")
        val sourceModuleInfos = loader.loadSourceModuleInfos()

        val modules = sourceModuleInfos.map {
            KotlinModule(it, componentRegistrars = listOf(DeepCopyComponentRegistrar()))
        }
        
        modules.checkResult(
            loader.loadExpectModuleInfos(),
            checkGeneratedIr = true,
            executeEntries = true
        )
    }
}