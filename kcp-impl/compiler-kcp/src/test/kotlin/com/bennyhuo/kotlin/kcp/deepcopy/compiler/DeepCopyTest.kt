package com.bennyhuo.kotlin.kcp.deepcopy.compiler

import com.bennyhuo.kotlin.compiletesting.extensions.module.KotlinModule
import com.bennyhuo.kotlin.compiletesting.extensions.module.checkResult
import com.bennyhuo.kotlin.compiletesting.extensions.source.FileBasedModuleInfoLoader
import com.bennyhuo.kotlin.deepcopy.compiler.kcp.DeepCopyComponentRegistrar
import org.jetbrains.kotlin.compiler.plugin.ExperimentalCompilerApi
import org.junit.Test

@OptIn(ExperimentalCompilerApi::class)
class DeepCopyTest {

    @Test
    fun simple() {
        testBase("simple.kt")
    }
    @Test
    fun basic() {
        testBase("basic.kt")
    }

    @Test
    fun declaredDeepCopy() {
        testBase("declaredDeepCopy.kt")
    }

    @Test
    fun genericsWithDeepCopyableBounds() {
        testBase("GenericsWithDeepCopyableBounds.kt")
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
        loader.loadSourceModuleInfos().map {
            KotlinModule(it, componentRegistrars = listOf(DeepCopyComponentRegistrar()))
        }.checkResult(
            loader.loadExpectModuleInfos(),
            checkGeneratedIr = true,
            executeEntries = true
        )
    }
}