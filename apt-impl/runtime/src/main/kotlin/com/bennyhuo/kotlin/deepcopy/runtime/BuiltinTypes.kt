package com.bennyhuo.kotlin.deepcopy.runtime

object DeepCopyScope {
    inline fun <C : Collection<T>, T> C.deepCopy(block: (T) -> T = { it }): C {
        val newInstance = javaClass.getDeclaredConstructor().newInstance()
        return mapTo(newInstance as MutableCollection<T>, block) as C
    }

    /**
     * explicit type args for K, V will cause type inference error for star types, e.g. Map<*,*>.
     */
    inline fun <C : Map<*, *>> C.deepCopy(block: (Map.Entry<*, *>) -> Any? = { it.value }): C {
        val newInstance = javaClass.getDeclaredConstructor().newInstance()
        return mapValuesTo(newInstance as MutableMap<Any?, Any?>, block) as C
    }

}

