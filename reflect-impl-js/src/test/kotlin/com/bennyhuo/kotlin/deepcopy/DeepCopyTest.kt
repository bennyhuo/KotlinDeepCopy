package com.bennyhuo.kotlin.deepcopy

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotSame

/**
 * Created by benny at 2021/6/26 8:36.
 */
data class Point(var x: Int, var y: Int) : DeepCopyable

data class Text(
    var id: Long,
    var text: String,
    var point: Point
) : DeepCopyable

class DeepCopyTest {

    @Test
    fun basic() {
        val text = Text(0, "Kotlin", Point(10, 20))
        val newText = text.deepCopy().apply { id = 2 }

        assertNotSame(text, newText)
        assertNotSame(text.point, newText.point)

        newText.point.x = 100

        assertEquals(text.point.x, 10)
        assertEquals(newText.point.x, 100)
    }
}
