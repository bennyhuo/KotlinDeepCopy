package com.bennyhuo.kotlin.deepcopy.runtime

import org.junit.Test

/**
 * Created by Benny Huo on 2023/1/16
 */
class StarProjectionTest {
    @Test
    fun test() {
        val map: Map<*, *> = mutableMapOf<String, Int>("a" to 1, "b" to 2)
        println(map.deepCopy({ it }, { it }))
        val list: MutableList<*> = mutableListOf<Int>()
        println(list.deepCopy { it })
    }
}