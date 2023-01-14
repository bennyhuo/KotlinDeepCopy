package com.bennyhuo.kotlin.deepcopy.compiler.apt.adapter

import com.bennyhuo.kotlin.deepcopy.compiler.apt.meta.KComponent
import com.bennyhuo.kotlin.deepcopy.compiler.apt.utils.RUNTIME_PACKAGE
import com.bennyhuo.kotlin.deepcopy.compiler.apt.utils.escapedPackageName
import com.squareup.kotlinpoet.FileSpec

/**
 * Created by benny.
 */
abstract class BaseCollectionAdapter(component: KComponent) : BaseAdapter(component) {


    override fun addImport(builder: FileSpec.Builder) {
        builder.addImport(RUNTIME_PACKAGE, "deepCopy")
    }

    override fun addStatement(builder: StringBuilder) {
        builder.append("${component.name}${nullableMark}.${deepCopyCall()}, ")
    }

    abstract fun deepCopyCall(): String

}

class DeepCopyableCollectionAdapter(component: KComponent) : BaseCollectionAdapter(component) {

    override fun addImport(builder: FileSpec.Builder) {
        super.addImport(builder)

        val elementType = component.typeArgumentElements.single()
        if (elementType?.isDeepCopyable == true) {
            builder.addImport(elementType.escapedPackageName, "deepCopy")
        }
    }

    override fun deepCopyCall(): String {
        return "deepCopy${
            if (component.isTypeArgumentDeepCopyable(0)) " { it.deepCopy() }"
            else "()"
        }"
    }
}

class DeepCopyableMapAdapter(component: KComponent) : BaseCollectionAdapter(component) {
    private val bodyForDeepCopyable = "{ it.deepCopy() }"
    private val bodyForNonDeepCopyable = "{ it }"

    override fun addImport(builder: FileSpec.Builder) {
        super.addImport(builder)

        val keyType = component.typeArgumentElements[0]
        if (keyType?.isDeepCopyable == true) {
            builder.addImport(keyType.escapedPackageName, "deepCopy")
        }

        val valueType = component.typeArgumentElements[1]
        if (valueType?.isDeepCopyable == true) {
            builder.addImport(valueType.escapedPackageName, "deepCopy")
        }
    }

    override fun deepCopyCall(): String {
        return "deepCopy(${
            (0..1).joinToString {
                if (component.isTypeArgumentDeepCopyable(it)) bodyForDeepCopyable
                else bodyForNonDeepCopyable
            }
        })"
    }
}