package com.bennyhuo.kotlin.deepcopy.compiler.apt.adapter

import com.bennyhuo.kotlin.deepcopy.compiler.apt.meta.KComponent
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
        component.typeElement?.isCollectionType == true -> {
            DeepCopyableCollectionAdapter(component)
        }
        component.typeElement?.isMapType == true -> {
            DeepCopyableMapAdapter(component)
        }
        else -> {
            NoDeepCopyAdapter(component)
        }
    }
}