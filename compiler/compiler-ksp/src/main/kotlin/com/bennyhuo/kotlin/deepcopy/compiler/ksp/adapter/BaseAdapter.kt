package com.bennyhuo.kotlin.deepcopy.compiler.ksp.adapter

import com.bennyhuo.kotlin.deepcopy.compiler.ksp.isSupportedCollectionType
import com.bennyhuo.kotlin.deepcopy.compiler.ksp.isSupportedMapType
import com.bennyhuo.kotlin.deepcopy.compiler.ksp.meta.KComponent
import com.squareup.kotlinpoet.FileSpec

/**
 * Created by benny.
 */
abstract class BaseAdapter(
    val component: KComponent
) {

    protected val nullableMark = if (component.type.isNullable) "?" else ""

    abstract fun addImport(builder: FileSpec.Builder)

    abstract fun addStatement(builder: StringBuilder)

}

fun Adapter(component: KComponent): BaseAdapter {
    return when {
        component.isDeepCopyable -> {
            DeepCopyAdapter(component)
        }
        component.declaration.isSupportedCollectionType -> {
            DeepCopyableCollectionAdapter(component)
        }
        component.declaration.isSupportedMapType -> {
            DeepCopyableMapAdapter(component)
        }
        else -> {
            NoDeepCopyAdapter(component)
        }
    }
}