package com.bennyhuo.kotlin.deepcopy.compiler

import com.bennyhuo.kotlin.deepcopy.reflect.DeepCopyable
import com.bennyhuo.kotlin.deepcopy.reflect.deepCopy
import org.junit.Test

data class Location(var lat: Double, var lng: Double)

data class Company(var name: String, var location: Location) : DeepCopyable

data class Speaker(var name: String, var age: Int, var company: Company) : DeepCopyable

data class Talk(var name: String, var speaker: Speaker) : DeepCopyable


data class Point(var x: Int, var y: Int): DeepCopyable
data class Text(var id: Long, var text: String, var point: Point): DeepCopyable

class DeepCopyTest {

    @Test
    fun test0() {
        val text = Text(0, "Kotlin", Point(10, 20))
        val newText = text.copy(1)
        newText.point.x = 100
        println(text)

        val newText2 = text.deepCopy().apply { id = 2 }
        newText2.point.x = 200
        println(text)
    }

    @Test
    fun test() {
        val talk = Talk(
            "如何优雅地使用数据类",
            Speaker(
                "bennyhuo 不是算命的",
                1,
                Company(
                    "猿辅导",
                    Location(39.9, 116.3)
                )
            )
        )

        val copiedTalk = talk.deepCopy()

        assert(talk.speaker !== copiedTalk.speaker)
        assert(talk.speaker.company !== copiedTalk.speaker.company)
        assert(talk.speaker.company.location === copiedTalk.speaker.company.location)
    }
}