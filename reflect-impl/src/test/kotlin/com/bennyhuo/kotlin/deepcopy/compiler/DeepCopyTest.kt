package com.bennyhuo.kotlin.deepcopy.compiler

import com.bennyhuo.kotlin.deepcopy.reflect.deepCopy
import org.junit.Test


data class Speaker(val name: String, val age: Int)

data class Talk(val name: String, val speaker: Speaker)

class DeepCopyTest {
    @Test
    fun test() {
        val talk = Talk("DataClass in Action", Speaker("Benny Huo", 30))
        val newTalk = talk.deepCopy()
        assert(talk == newTalk)
        assert(talk !== newTalk)
    }
}