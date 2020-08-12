package com.bennyhuo.kotlin.deepcopy.runtime

import java.lang.reflect.Method
import kotlin.reflect.KClass

@Suppress("UNCHECKED_CAST")
object DeepCopyScope {

    inline fun <reified T : Any?> Array<T>.deepCopy(): Array<T> {
        return map {
            ElementDeepCopyHandler.run {
                (it as? Any)?.deepCopyElement() as T
            }
        }.toTypedArray()
    }

    fun CharArray.deepCopy() = copyOf()
    fun ByteArray.deepCopy() = copyOf()
    fun ShortArray.deepCopy() = copyOf()
    fun IntArray.deepCopy() = copyOf()
    fun FloatArray.deepCopy() = copyOf()
    fun DoubleArray.deepCopy() = copyOf()
    fun BooleanArray.deepCopy() = copyOf()

    fun <C : Collection<T?>, T> C.deepCopy(): C {
        val newInstance = try {
            javaClass.getDeclaredConstructor().newInstance()
        } catch (e: Exception) {
            ArrayList<T>(this.size)
        }
        return (this as Collection<Any?>).mapTo(newInstance as MutableCollection<Any?>) {
            ElementDeepCopyHandler.run {
                it?.deepCopyElement()
            }
        } as C
    }

    /**
     * explicit type args for K, V will cause type inference error for star types, e.g. Map<*,*>.
     */
    fun <C : Map<*, V?>, V> C.deepCopy(): C {
        val newInstance: C = try {
            javaClass.getDeclaredConstructor().newInstance()
        } catch (e: Exception) {
            hashMapOf<Any, V>() as C
        }
        return (this as Map<Any?, Any?>).mapValuesTo(newInstance as MutableMap<Any?, Any?>) {
            ElementDeepCopyHandler.run {
                it.value?.deepCopyElement()
            }
        } as C
    }

    object ElementDeepCopyHandler {
        private val deepCopyMethodCache = HashMap<Class<*>, Method>()
        private val deepCopyMethodCreator = { cls: Class<*> ->
            var packageName = cls.`package`.name
            if (packageName == "kotlin") {
                packageName = "com.bennyhuo.kotlin.deepcopy.builtin"
            }
            val generatedDeepCopyClass = Class.forName("$packageName.${cls.simpleName}__DeepCopyKt")
            generatedDeepCopyClass.getDeclaredMethod("deepCopy", cls)
        }

        fun <T : Any> T?.deepCopyElement(): T? {
            if (this == null) return null
            return when (this) {
                is Map<*, *> -> {
                    this.deepCopy() as T
                }
                is Collection<*> -> {
                    this.deepCopy() as T
                }
                else -> {
                    if (isDeepCopySupported(this::class)) {
                        callDeepCopy(this)
                    } else {
                        this
                    }
                }
            }
        }

        private fun <T : Any> callDeepCopy(t: T): T {
            val deepCopyMethod = deepCopyMethodCache[t.javaClass] ?: deepCopyMethodCreator(t.javaClass)
                .also {
                    deepCopyMethodCache[t.javaClass] = it
                }
            return deepCopyMethod.invoke(null, t) as T
        }
    }

    private val deepCopyClass by lazy {
        Class.forName("com.bennyhuo.kotlin.deepcopy.DeepCopy")
    }

    private val supportedTypes by lazy {
        deepCopyClass.getDeclaredField("supportedTypes").also { it.isAccessible = true }.get(null) as Set<KClass<*>>
    }

    private fun <T : Any> isDeepCopySupported(kClass: KClass<T>) = kClass in supportedTypes

}

