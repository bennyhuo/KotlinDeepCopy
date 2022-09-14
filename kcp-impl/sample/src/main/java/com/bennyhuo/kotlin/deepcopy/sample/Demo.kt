package com.bennyhuo.kotlin.deepcopy.sample

import com.bennyhuo.kotlin.deepcopy.annotations.DeepCopy

@DeepCopy
data class User(val id: Long, val name: String)

fun main() {
    val user = User(0, "bennyhuo")
    val deepCopiedUser = user.deepCopy()
}