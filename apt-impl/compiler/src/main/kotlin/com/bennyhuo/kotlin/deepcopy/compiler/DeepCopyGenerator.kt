package com.bennyhuo.kotlin.deepcopy.compiler

import com.bennyhuo.aptutils.types.packageName
import com.bennyhuo.aptutils.types.simpleName
import com.bennyhuo.aptutils.utils.writeToFile
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.KModifier

class DeepCopyGenerator(val kTypeElement: KTypeElement){

    companion object {
        const val POSIX = "$\$DeepCopy"
    }

    fun generate(){
        val fileSpecBuilder = FileSpec.builder(kTypeElement.packageName(), kTypeElement.simpleName() + POSIX)
        val functionBuilder = FunSpec.builder("deepCopy")
            .receiver(kTypeElement.kotlinClassName)
            .addModifiers(KModifier.PUBLIC)
            .returns(kTypeElement.kotlinClassName)

        val statementStringBuilder = StringBuilder("%T(")
        val parameters = ArrayList<Any>()
        kTypeElement.components.forEach { component ->
            statementStringBuilder.append("%L, ")
            if(component.typeElement?.canDeepCopy == true){
                parameters.add("${component.name}.deepCopy()")
            } else {
                parameters.add(component.kotlinClassName)
            }
        }
        statementStringBuilder.setCharAt(statementStringBuilder.lastIndex - 1, ')')

        functionBuilder.addStatement("return $statementStringBuilder", kTypeElement.kotlinClassName, *(parameters.toTypedArray()))

        fileSpecBuilder.addFunction(functionBuilder.build()).build().writeToFile()
    }
}