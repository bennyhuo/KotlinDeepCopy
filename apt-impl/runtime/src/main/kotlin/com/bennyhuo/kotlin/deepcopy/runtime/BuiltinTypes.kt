package com.bennyhuo.kotlin.deepcopy.runtime

object DeepCopyScope {
    inline fun <C: MutableCollection<T>, T> C.deepCopy(block: (T) -> T = { it }): C {
        val newInstance = javaClass.getDeclaredConstructor().newInstance()
        return mapTo(newInstance, block)
    }

    inline fun <K, V> Map<K, V>.deepCopy(block: (Map.Entry<K, V>) -> V = { it.value }): Map<K, V> {
        val newInstance = javaClass.getDeclaredConstructor().newInstance()
        return mapValuesTo(newInstance as MutableMap<K, V>, block)
    }
}

