package com.bennyhuo.kotlin.deepcopy.runtime

/**
 * Created by benny.
 */
fun <T> Collection<T>.deepCopy(elementDeepCopyBlock: (T) -> T) = map(elementDeepCopyBlock)

fun <K, V> Map<K, V>.deepCopy(valueDeepCopyBlock: (V) -> V) = mapValues {
    valueDeepCopyBlock(it.value)
}