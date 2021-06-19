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
        val fileSpecBuilder = FileSpec.builder(escapeStdlibPackageName(kTypeElement.packageName()), kTypeElement.simpleName() + POSIX)
        val functionBuilder = FunSpec.builder("deepCopy")
            .receiver(kTypeElement.kotlinClassName)
            .addModifiers(KModifier.PUBLIC)
            .returns(kTypeElement.kotlinClassName)

        functionBuilder.addTypeVariables(kTypeElement.typeVariablesWithoutVariance)
            .addAnnotation(JvmOverloads::class)

        val suppressWarnings = hashSetOf<String>()

        val statementStringBuilder = StringBuilder("%T(")
        val parameters = ArrayList<Any>()
        println(kTypeElement)
        println(kTypeElement.components)
        kTypeElement.components.forEach { component ->
            statementStringBuilder.append("%L, ")
            when {
                component.type is TypeVariableName -> {
                    val elementDeepCopyHandler = ClassName.bestGuess("com.bennyhuo.kotlin.deepcopy.runtime.DeepCopyScope.ElementDeepCopyHandler")
                    //cannot tell whether the type variable is nullable from declaration.
                    parameters.add(CodeBlock.of("%T().run{ (${component.name} as Any?)?.deepCopyElement() as %T  }", elementDeepCopyHandler, component.type))

                    suppressWarnings += "UNCHECKED_CAST"
                }
                component.typeElement?.canDeepCopy == true -> {
                    val typeElement = component.typeElement!!
                    when {
                        typeElement.isDataType ->{
                            val deepCopyMethod = MemberName(escapeStdlibPackageName(component.typeElement!!.packageName()), "deepCopy")
                            if(component.type.isNullable){
                                parameters.add(CodeBlock.of("${component.name}?.%M()", deepCopyMethod))
                            } else {
                                parameters.add(CodeBlock.of("${component.name}.%M()", deepCopyMethod))
                            }
                        }
                        typeElement.isMapType || typeElement.isCollectionType->{
                            val deepCopyScope = ClassName.bestGuess("com.bennyhuo.kotlin.deepcopy.runtime.DeepCopyScope")
                            if(component.type.isNullable){
                                parameters.add(CodeBlock.of("%T.run{ ${component.name}?.deepCopy() }", deepCopyScope))
                            } else {
                                parameters.add(CodeBlock.of("%T.run{ ${component.name}.deepCopy() }", deepCopyScope))
                            }
                        }
                    }
                }
                else -> parameters.add(component.name)
            }
            functionBuilder.addParameter(ParameterSpec.builder(component.name, component.type).defaultValue("this.${component.name}").build())
        }
        println(statementStringBuilder)
        statementStringBuilder.setCharAt(statementStringBuilder.lastIndex - 1, ')')

        println(statementStringBuilder)
        functionBuilder.addStatement("return $statementStringBuilder", kTypeElement.kotlinClassName, *(parameters.toTypedArray()))

        suppressWarnings.forEach {
            functionBuilder.addAnnotation(AnnotationSpec.builder(Suppress::class).addMember("%S", it).build())
        }

        fileSpecBuilder.addFunction(functionBuilder.build()).build().writeToFile()
    }
}