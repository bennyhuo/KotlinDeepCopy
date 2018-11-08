package com.bennyhuo.kotlin.deepcopy.reflect

import org.junit.Test


data class Speaker(val id: Int, val name: String, val age: Int)

data class Talk(val id: Int, val name: String, val speaker: Speaker)

class DeepCopyTest{
    @Test
    fun test(){
        val talk = Talk(0, "DataClass in Action", Speaker(1, "Benny Huo", 30))
        val newTalk = talk.deepCopy()
        assert(talk == newTalk)
        assert(talk !== newTalk)
    }
}