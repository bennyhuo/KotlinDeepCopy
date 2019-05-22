package com.bennyhuo.kotlin.deepcopy.compiler

import com.bennyhuo.aptutils.types.packageName
import com.bennyhuo.aptutils.types.simpleName
import com.bennyhuo.aptutils.utils.writeToFile
import com.squareup.kotlinpoet.*

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

        functionBuilder.addTypeVariables(kTypeElement.typeVariablesWithoutVariance)

        val statementStringBuilder = StringBuilder("%T(")
        val parameters = ArrayList<Any>()
        kTypeElement.components.forEach { component ->
            statementStringBuilder.append("%L, ")
            if(component.typeElement?.canDeepCopy == true){
                val typeElement = component.typeElement!!
                when {
                    typeElement.isMapType ->{
                        val deepCopyMethod = MemberName("com.bennyhuo.kotlin.deepcopy.runtime.DeepCopyScope", "deepCopy")
                        if(typeElement.elementType?.canDeepCopy == true){
                            val elementDeepCopyMethod = MemberName(typeElement.elementClassName!!.packageName, "deepCopy")
                            parameters.add(CodeBlock.of("${component.name}?.%M{ it?.%M }", deepCopyMethod, elementDeepCopyMethod))
                        } else {
                            parameters.add(CodeBlock.of("${component.name}?.%M()", deepCopyMethod))
                        }
                    }
                    typeElement.isCollectionType ->{
                        val deepCopyMethod = MemberName("com.bennyhuo.kotlin.deepcopy.runtime.DeepCopyScope", "deepCopy")
                        if(component.type.isNullable){
                            parameters.add(CodeBlock.of("${component.name}?.%M()", deepCopyMethod))
                        } else {
                            parameters.add(CodeBlock.of("${component.name}.%M()", deepCopyMethod))
                        }
                    }
                    else -> {
                        val deepCopyMethod = MemberName(component.typeElement!!.packageName(), "deepCopy")
                        if(component.type.isNullable){
                            parameters.add(CodeBlock.of("${component.name}?.%M()", deepCopyMethod))
                        } else {
                            parameters.add(CodeBlock.of("${component.name}.%M()", deepCopyMethod))
                        }
                    }
                }
            } else {
                parameters.add(component.name)
            }
            functionBuilder.addParameter(ParameterSpec.builder(component.name, component.type).defaultValue("this.${component.name}").build())
        }
        statementStringBuilder.setCharAt(statementStringBuilder.lastIndex - 1, ')')

        functionBuilder.addStatement("return $statementStringBuilder", kTypeElement.kotlinClassName, *(parameters.toTypedArray()))

        fileSpecBuilder.addFunction(functionBuilder.build()).build().writeToFile()
    }
}