package com.bennyhuo.kotlin.deepcopy.sample.array

import com.bennyhuo.kotlin.deepcopy.annotations.DeepCopy

@DeepCopy
data class DeepCopied(val name: String)

class NoDeepCopy(val value: Int)

@DeepCopy
data class Arrays(
    val chars: CharArray,
    val bytes: ByteArray,
    val shorts: ShortArray,
    val ints: IntArray,
    val floats: FloatArray,
    val doubleArray: DoubleArray,
    val booleans: BooleanArray,
    val strings: Array<String>,
    val deepCopieds: Array<DeepCopied>,
    val noDeepCopies: Array<NoDeepCopy>
)

fun main() {
    val arrays = Arrays(
        charArrayOf('a', 'b'),
        byteArrayOf(1, 2),
        shortArrayOf(1, 2),
        intArrayOf(1, 2),
        floatArrayOf(1f, 2f),
        doubleArrayOf(1.0, 2.0),
        booleanArrayOf(true, false),
        arrayOf("Hello", "World"),
        arrayOf(DeepCopied("Hi"), DeepCopied("There")),
        arrayOf(NoDeepCopy(1), NoDeepCopy(2))
    )

    val copiedArrays = arrays.deepCopy()
    println(arrays.booleans !== copiedArrays.booleans)
    println(arrays.strings !== copiedArrays.strings)
    println(arrays.ints !== copiedArrays.ints)
    println(arrays.deepCopieds[0] !== copiedArrays.deepCopieds[0])
    println(arrays.noDeepCopies[0] === copiedArrays.noDeepCopies[0])
}