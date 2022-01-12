@file:Suppress("UNCHECKED_CAST", "IMPLICIT_CAST_TO_ANY")
package com.bennyhuo.kotlin.deepcopy

/**
 * Created by benny at 2022/1/12 8:49 PM.
 */
private fun <T> deepCopyMapper(value: T): T {
    return if (value is DeepCopiable<*>) {
        value.deepCopy() as T
    } else {
        when(value) {
            is Set<*> -> value.deepCopy()
            is List<*> -> value.deepCopy()
            is Collection<*> -> value.deepCopy()
            is Iterable<*> -> value.deepCopy()
            else -> value
        } as T
    }
}

fun <T> Iterable<T>.deepCopy(): MutableIterable<T> = mapTo(ArrayList(), ::deepCopyMapper)

fun <T> Collection<T>.deepCopy(): MutableCollection<T> = mapTo(ArrayList(), ::deepCopyMapper)

fun <T> List<T>.deepCopy(): MutableList<T> = mapTo(ArrayList(), ::deepCopyMapper)

fun <T> Set<T>.deepCopy(): Set<T> = map(::deepCopyMapper).toSet()