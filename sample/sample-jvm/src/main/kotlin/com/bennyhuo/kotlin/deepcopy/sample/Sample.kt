package com.bennyhuo.kotlin.deepcopy.sample

import com.bennyhuo.kotlin.deepcopy.annotations.DeepCopy

@DeepCopy
data class District(var name: String)

@DeepCopy
data class Location(var lat: Double, var lng: Double)

@DeepCopy
data class Company(
    var name: String,
    var location: Location,
    var district: District
)

@DeepCopy
data class Speaker(var name: String, var age: Int, var company: Company)

@DeepCopy
data class Talk(var name: String, var speaker: Speaker)

fun main(args: Array<String>) {
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
    copiedTalk.name = "Kotlin 编译器插件：我们不期待"
    copiedTalk.speaker.company = Company(
        "猿辅导",
        Location(39.9, 116.3),
        District("华鼎世家对面")
    )
    println(talk)
    println(copiedTalk)
    println(talk === copiedTalk)
    println(talk.speaker === copiedTalk.speaker)
}