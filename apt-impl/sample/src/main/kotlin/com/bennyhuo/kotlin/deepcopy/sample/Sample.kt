package com.bennyhuo.kotlin.deepcopy.sample


data class Speaker(val id: Int, val name: String, val age: Int)

data class Talk(val id: Int, val name: String, val speaker: Speaker)