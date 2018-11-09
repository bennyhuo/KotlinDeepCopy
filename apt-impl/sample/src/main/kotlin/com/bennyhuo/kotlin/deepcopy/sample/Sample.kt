package com.bennyhuo.kotlin.deepcopy.sample

import com.bennyhuo.kotlin.deepcopy.annotations.DeepCopy

@DeepCopy
data class Speaker(val id: Int, val name: String, val age: Int)

@DeepCopy
data class Talk(val id: Int, val name: String, val speaker: Speaker)

fun main(args: Array<String>) {
    val talk = Talk(0, "Data class in Action", Speaker(1, "Benny Huo", 30))
    val copiedTalk = talk.deepCopy()
    assert(talk == copiedTalk)
    assert(talk !== copiedTalk)
    assert(talk.speaker == copiedTalk.speaker)
    assert(talk.speaker !== copiedTalk.speaker)
}