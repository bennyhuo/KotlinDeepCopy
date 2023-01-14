package com.benyhuo.kotlin.deepcopy.compiler

import com.bennyhuo.kotlin.compiletesting.extensions.module.KotlinModule
import com.bennyhuo.kotlin.compiletesting.extensions.source.SourceModuleInfo
import com.bennyhuo.kotlin.deepcopy.compiler.ksp.DeepCopySymbolProcessorProvider
import java.io.File
import org.junit.Test

/**
 * Created by benny at 2021/6/21 7:00.
 */
class KspTest : BaseTest() {
    @Test
    fun basic() = doTest()

    @Test
    fun collections() = doTest()

    @Test
    fun config() = doTest()

    @Test
    fun generics() = doTest()

    @Test
    fun genericsWithDeepCopyableBounds() = doTest()

    @Test
    fun innerClasses() = doTest()

    @Test
    fun modules() = doTest()

    @Test
    fun nullables() = doTest()

    @Test
    fun recursive() = doTest()

    @Test
    fun typeAliases() = doTest()

    override val testCaseDir: File = File("testData/ksp")

    override fun createKotlinModule(moduleInfo: SourceModuleInfo): KotlinModule {
        return KotlinModule(moduleInfo, symbolProcessorProviders = listOf(DeepCopySymbolProcessorProvider()))
    }
}