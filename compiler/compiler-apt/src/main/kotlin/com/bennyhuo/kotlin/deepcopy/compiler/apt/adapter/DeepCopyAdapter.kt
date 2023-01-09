package com.bennyhuo.kotlin.deepcopy.compiler.apt.adapter

import com.bennyhuo.kotlin.deepcopy.compiler.apt.meta.KComponent
import com.bennyhuo.kotlin.deepcopy.compiler.apt.utils.escapedPackageName
import com.squareup.kotlinpoet.FileSpec

/**
 * Created by benny.
 */
class DeepCopyAdapter(component: KComponent) : BaseAdapter(component) {
    override fun addImport(builder: FileSpec.Builder) {
        if (component.isDeepCopyableClass) {
            builder.addImport(component.typeElement!!.escapedPackageName, "deepCopy")
        }
    }

    override fun addStatement(builder: StringBuilder) {
        builder.append("${component.name}${nullableMark}.deepCopy(), ")
    }
}