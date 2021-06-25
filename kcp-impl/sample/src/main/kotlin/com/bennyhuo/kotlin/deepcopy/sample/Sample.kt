package com.bennyhuo.kotlin.deepcopy.sample

import com.bennyhuo.kotlin.deepcopy.annotations.DeepCopy

@DeepCopy
data class District(val name: String)

@DeepCopy
data class Location(val lat: Double, val lng: Double)

@DeepCopy
data class Company(val name: String, val location: Location, val district: District)

@DeepCopy
data class Speaker(val name: String, val age: Int, val company: Company)

@DeepCopy
data class Talk(val name: String, val speaker: Speaker)

fun main(args: Array<String>) {
    val talk = Talk("Data class in Action", Speaker("Benny Huo", 30, Company("Tencent", Location(39.9, 116.3), District("中关村"))))
    val copiedTalk = talk.deepCopy()
    println(copiedTalk)
}