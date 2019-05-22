package com.bennyhuo.kotlin.deepcopy.runtime

import org.junit.Test

class BuiltinTypesTest{
    @Test
    fun test() {
        val map = mapOf(1 to mutableListOf(1, 2, 3), 2 to mutableListOf(3, 3, 2))
        DeepCopyScope.apply {
            val copied = map.deepCopy {
                it.value.deepCopy()
            }
            assert(map[1] !== copied[1])
            map.getValue(1) += 4
            assert(map.getValue(1).size == 4)
            assert(copied.getValue(1).size == 3)
        }
    }
}