package com.bennyhuo.kotlin.deepcopy.compiler.apt

import com.bennyhuo.aptutils.AptContext
import com.bennyhuo.aptutils.types.packageName
import com.bennyhuo.aptutils.types.simpleName
import com.bennyhuo.kotlin.deepcopy.compiler.apt.adapter.Adapter
import com.bennyhuo.kotlin.deepcopy.compiler.apt.meta.KTypeElement
import com.bennyhuo.kotlin.deepcopy.compiler.apt.utils.escapeStdlibPackageName
import com.squareup.kotlinpoet.AnnotationSpec
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.ParameterSpec

class DeepCopyGenerator(val kTypeElement: KTypeElement){

    companion object {
        const val POSIX = "$\$DeepCopy"
    }

    fun generate(){
        val fileSpecBuilder = FileSpec.builder(
            escapeStdlibPackageName(kTypeElement.packageName()),
            kTypeElement.simpleName() + POSIX
        )
        val functionBuilder = FunSpec.builder("deepCopy")
            .receiver(kTypeElement.kotlinClassName)
            .addModifiers(KModifier.PUBLIC)
            .returns(kTypeElement.kotlinClassName)
            .addOriginatingElement(kTypeElement.typeElement)

        functionBuilder.addTypeVariables(kTypeElement.typeVariablesWithoutVariance)
            .addAnnotation(JvmOverloads::class)

        val suppressWarnings = hashSetOf<String>()

        val statementStringBuilder = StringBuilder("%T(")

        kTypeElement.components.forEach { component ->
            val adapter = Adapter(component)
            adapter.addImport(fileSpecBuilder)
            adapter.addStatement(statementStringBuilder)

            functionBuilder.addParameter(
                ParameterSpec.builder(component.name, component.type)
                    .defaultValue("this.${component.name}").build()
            )
        }
        statementStringBuilder.setCharAt(statementStringBuilder.lastIndex - 1, ')')

        functionBuilder.addStatement("return $statementStringBuilder", 
            kTypeElement.kotlinClassName)

        suppressWarnings.forEach {
            functionBuilder.addAnnotation(AnnotationSpec.builder(Suppress::class).addMember("%S", it).build())
        }

        fileSpecBuilder.addFunction(functionBuilder.build()).build().writeTo(AptContext.filer)
    }
}