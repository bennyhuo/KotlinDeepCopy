package com.bennyhuo.kotlin.deepcopy.sample.nullable

import com.bennyhuo.kotlin.deepcopy.annotations.DeepCopy
import com.bennyhuo.kotlin.deepcopy.builtin.deepCopy

@DeepCopy
data class User(val name: String)

@DeepCopy
data class Nullables(val user: User?, val list: List<User>?)

fun main() {
    val pair: Pair<String, User?> = "Hello" to null//User("Benny")
    val copied = pair.deepCopy()
    println(pair.second === copied.second)
}