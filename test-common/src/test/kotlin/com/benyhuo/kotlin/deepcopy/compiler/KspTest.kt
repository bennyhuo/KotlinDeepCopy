package com.benyhuo.kotlin.deepcopy.compiler

import com.tschuchort.compiletesting.KotlinCompilation
import com.tschuchort.compiletesting.kspSourcesDir
import org.jetbrains.kotlin.compiler.plugin.ExperimentalCompilerApi
import org.junit.Test

/**
 * Created by benny at 2021/6/21 7:00.
 */
@OptIn(ExperimentalCompilerApi::class)
class KspTest {
    
    fun doTest(path: String) = doTest(path, compilationWithKsp(), KotlinCompilation::kspSourcesDir)
    
    @Test
    fun testBasic() {
        doTest("testData/Basic.kt")
    }

    @Test
    fun testGenerics() {
        doTest("testData/Generics.kt")
    }

    @Test
    fun testGenericsWithDeepCopyableBounds() {
        doTest("testData/GenericsWithDeepCopyableBounds.kt")
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
}