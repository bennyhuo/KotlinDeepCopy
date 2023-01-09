package com.bennyhuo.kotlin.deepcopy.compiler.ksp.adapter

import com.bennyhuo.kotlin.deepcopy.compiler.ksp.escapedPackageName
import com.bennyhuo.kotlin.deepcopy.compiler.ksp.meta.KComponent
import com.squareup.kotlinpoet.FileSpec

/**
 * Created by benny.
 */
class DeepCopyAdapter(component: KComponent) : BaseAdapter(component) {
    override fun addImport(builder: FileSpec.Builder) {
        if (component.isDeepCopyableClass) {
            builder.addImport(component.declaration.escapedPackageName, "deepCopy")
        }
    }

    override fun addStatement(builder: StringBuilder) {
        builder.append("${component.name}${nullableMark}.deepCopy(), ")
    }
}