package com.benyhuo.kotlin.deepcopy.compiler

import org.junit.Test

/**
 * Created by benny at 2021/6/21 7:00.
 */
class KaptTest {
    
    fun doTest(path: String) = doTest(path, compilationWithKapt())
    
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
}