package com.bennyhuo.kotlin.js

import com.bennyhuo.kotlin.deepcopy.DeepCopyable
import com.bennyhuo.kotlin.deepcopy.deepCopy

/**
 * Created by benny at 2021/6/26 8:36.
 */
data class Point(var x: Int, var y: Int) : DeepCopyable

data class Text(
    var id: Long,
    var text: String,
    var point: Point
) : DeepCopyable

fun main() {
    val text = Text(0, "Kotlin", Point(10, 20))
    val newText = text.deepCopy().apply { id = 2 }
    newText.point.x = 100

    println(text.point.x == 10)
    println(newText.point.x == 100)
}