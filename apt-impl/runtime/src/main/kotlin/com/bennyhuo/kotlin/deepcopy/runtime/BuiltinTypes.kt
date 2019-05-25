package com.bennyhuo.kotlin.deepcopy.runtime

import java.lang.reflect.Method
import kotlin.reflect.KClass

@Suppress("UNCHECKED_CAST")
object DeepCopyScope {

    fun <C : Collection<T?>, T> C.deepCopy(): C {
        val newInstance = javaClass.getDeclaredConstructor().newInstance()
        val elementDeepCopyHandler = ElementDeepCopyHandler()
        return (this as Collection<Any?>).mapTo(newInstance as MutableCollection<Any?>){
            elementDeepCopyHandler.run {
                it?.deepCopyElement()
            }
        } as C
    }

    /**
     * explicit type args for K, V will cause type inference error for star types, e.g. Map<*,*>.
     */
    fun <C : Map<*, V?>, V> C.deepCopy(): C {
        val newInstance = javaClass.getDeclaredConstructor().newInstance()
        val elementDeepCopyHandler = ElementDeepCopyHandler()
        return (this as Map<Any?, Any?>).mapValuesTo(newInstance as MutableMap<Any?, Any?>){
            elementDeepCopyHandler.run {
                it.value?.deepCopyElement()
            }
        } as C
    }

    private class ElementDeepCopyHandler {
        private val deepCopyMethodCache = HashMap<Class<*>, Method>().withDefault {
            val generatedDeepCopyClass = Class.forName( "${it.canonicalName}__DeepCopyKt")
            generatedDeepCopyClass.getDeclaredMethod("deepCopy", it)
        }

        fun <T: Any> T?.deepCopyElement(): T? {
            if(this == null) return null
            return when (this) {
                is Map<*, *> -> {
                    this.deepCopy() as T
                }
                is Collection<*> -> {
                    this.deepCopy() as T
                }
                else -> {
                    if(isDeepCopySupported(this::class)){
                        callDeepCopy(this)
                    } else {
                        this
                    }
                }
            }
        }

        private fun <T: Any> callDeepCopy(t: T): T{
            val deepCopyMethod = deepCopyMethodCache.getValue(t.javaClass)
            return deepCopyMethod.invoke(null, t) as T
        }
    }

    private val deepCopyClass by lazy {
        Class.forName("com.bennyhuo.kotlin.deepcopy.DeepCopy")
    }

    private val supportedTypes by lazy {
        deepCopyClass.getDeclaredField("supportedTypes").also { it.isAccessible = true }.get(null) as Set<KClass<*>>
    }

    private fun <T: Any> isDeepCopySupported(kClass: KClass<T>) = kClass in supportedTypes

}

