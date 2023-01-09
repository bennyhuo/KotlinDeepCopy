package com.bennyhuo.kotlin.deepcopy.compiler.apt.adapter

import com.bennyhuo.kotlin.deepcopy.compiler.apt.meta.KComponent
import com.squareup.kotlinpoet.FileSpec

/**
 * Created by benny.
 */
class NoDeepCopyAdapter(
    component: KComponent
): BaseAdapter(component){
    override fun addImport(builder: FileSpec.Builder) = Unit

    override fun addStatement(builder: StringBuilder) {
        builder.append("${component.name}, ")
    }
}