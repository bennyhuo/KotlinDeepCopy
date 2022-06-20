package com.bennyhuo.kotlin.js

import com.bennyhuo.kotlin.deepcopy.DeepCopyable
import com.bennyhuo.kotlin.deepcopy.deepCopy
import kotlin.math.abs

/**
 * Created by benny at 2021/6/26 8:36.
 */
data class Point(var x: Int, var y: Int) : DeepCopyable

data class Text(
    var id: Long,
    var text: String,
    var point: Point
) : DeepCopyable

class Test {
    fun a(): Int {
        return 0
    }

    fun a1(a: Int) = 0

    fun a2(a: Int, b: String) = 1
}

fun main() {
    println("Hello Kotlin")
    println(abs("component1".hashCode()).toString(36))
    println(abs("component2".hashCode()).toString(36))
    val t = Test()
    val text = Text(0, "Kotlin", Point(10, 20))
    val newText = text.deepCopy().apply { id = 2 }
    newText.point.x = 100

    println(text.point.x == 10)
    println(newText.point.x == 100)
}