package com.bennyhuo.kotlin.deepcopy.compiler

import com.bennyhuo.kotlin.deepcopy.reflect.DeepCopyable
import com.bennyhuo.kotlin.deepcopy.reflect.deepCopy
import org.junit.Test

data class District(var name: String)

data class Location(var lat: Double, var lng: Double)

data class Company(var name: String, var location: Location, var district: District): DeepCopyable

data class Speaker(var name: String, var age: Int, var company: Company): DeepCopyable

data class Talk(var name: String, var speaker: Speaker): DeepCopyable

class DeepCopyTest {
    @Test
    fun test() {
        val talk = Talk(
            "如何优雅地使用数据类",
            Speaker(
                "bennyhuo 不是算命的",
                1,
                Company(
                    "猿辅导",
                    Location(39.9, 116.3),
                    District("北京郊区")
                )
            )
        )

        val copiedTalk = talk.deepCopy()
//        copiedTalk.name = "Kotlin 编译器插件：我们不期待"
//        copiedTalk.speaker.company = Company(
//            "猿辅导",
//            Location(39.9, 116.3),
//            District("华鼎世家对面")
//        )

        assert(talk.speaker !== copiedTalk.speaker)
        assert(talk.speaker.company !== copiedTalk.speaker.company)
        assert(talk.speaker.company.location === copiedTalk.speaker.company.location)
    }
}