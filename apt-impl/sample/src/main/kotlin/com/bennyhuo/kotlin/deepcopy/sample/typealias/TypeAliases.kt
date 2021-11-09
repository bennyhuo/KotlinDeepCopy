package com.bennyhuo.kotlin.deepcopy.sample.`typealias`

import com.bennyhuo.kotlin.deepcopy.annotations.DeepCopy

typealias X<K, V> = Map<K,V>

@DeepCopy
data class GenericParameter(val map: X<String, List<String>>)

@DeepCopy
data class GenericParameterT<K: Number, V>(val map: X<K, V>)

fun main() {
    val value = GenericParameterT<Int, String>(hashMapOf(2 to "Hello"))
    val copied = value.deepCopy()
    println(value.map === copied.map)
}