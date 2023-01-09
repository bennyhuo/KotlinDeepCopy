package com.bennyhuo.kotlin.deepcopy.compiler.ksp.adapter

import com.bennyhuo.kotlin.deepcopy.compiler.ksp.RUNTIME_PACKAGE
import com.bennyhuo.kotlin.deepcopy.compiler.ksp.deepCopyable
import com.bennyhuo.kotlin.deepcopy.compiler.ksp.escapedPackageName
import com.bennyhuo.kotlin.deepcopy.compiler.ksp.meta.KComponent
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

        val elementType = component.ksType.arguments.single().type!!.resolve().declaration
        if (elementType.deepCopyable) {
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

        val keyType = component.ksType.arguments[0].type!!.resolve().declaration
        if (keyType.deepCopyable) {
            builder.addImport(keyType.escapedPackageName, "deepCopy")
        }

        val valueType = component.ksType.arguments[1].type!!.resolve().declaration
        if (valueType.deepCopyable) {
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