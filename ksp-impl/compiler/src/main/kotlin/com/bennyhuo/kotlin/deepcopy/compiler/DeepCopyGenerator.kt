package com.bennyhuo.kotlin.deepcopy.compiler

import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.Modifier
import com.squareup.kotlinpoet.*
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.squareup.kotlinpoet.ksp.*

/**
 * Created by benny.
 */
class DeepCopyGenerator() {

    fun generate(deepCopyTypes: Set<KSClassDeclaration>) {
        deepCopyTypes.forEach { dataClass: KSClassDeclaration ->

            val typeParameterResolver = dataClass.typeParameters.toTypeParameterResolver()
            val dataClassName = dataClass.toClassName().let { className ->
                if (dataClass.typeParameters.isNotEmpty()) {
                    className.parameterizedBy(
                        dataClass.typeParameters.map {
                            it.toTypeVariableName(typeParameterResolver)
                        })
                } else className
            }
            val fileSpecBuilder = FileSpec.builder(
                escapeStdlibPackageName(dataClass.packageName.asString()),
                "${dataClass.simpleName.asString()}$\$DeepCopy"
            )
            val functionBuilder = FunSpec.builder("deepCopy")
                .receiver(dataClassName)
                .addModifiers(KModifier.PUBLIC)
                .returns(dataClassName)
                .addAnnotation(JvmOverloads::class)
                .addTypeVariables(dataClass.typeParameters.map {
                    it.toTypeVariableName(typeParameterResolver).let { TypeVariableName(it.name, it.bounds) }
                }).also { builder ->
                    dataClass.containingFile?.let { builder.addOriginatingKSFile(it) }
                }

            val statementStringBuilder = StringBuilder("%T(")
            val parameters = ArrayList<Any>()

            dataClass.primaryConstructor!!.parameters.forEach { parameter ->
                statementStringBuilder.append("%L, ")
                if (Modifier.DATA in parameter.type.resolve().declaration.modifiers && parameter.type.resolve().declaration in deepCopyTypes) {
                    val deepCopyMethod =
                        MemberName(dataClass.packageName.asString(), "deepCopy")
                    if (parameter.type.resolve().isMarkedNullable) {
                        parameters.add(
                            CodeBlock.of(
                                "${parameter.name!!.asString()}?.%M()",
                                deepCopyMethod
                            )
                        )
                    } else {
                        parameters.add(
                            CodeBlock.of(
                                "${parameter.name!!.asString()}.%M()",
                                deepCopyMethod
                            )
                        )
                    }
                } else {
                    parameters.add(parameter.name!!.asString())
                }


                functionBuilder.addParameter(
                    ParameterSpec.builder(
                        parameter.name!!.asString(),
                        parameter.type.toTypeName(typeParameterResolver)
                    ).defaultValue("this.${parameter.name!!.asString()}").build()
                )
            }

            statementStringBuilder.setCharAt(statementStringBuilder.lastIndex - 1, ')')
            functionBuilder.addStatement(
                "return $statementStringBuilder",
                dataClassName,
                *(parameters.toTypedArray())
            )
            fileSpecBuilder.addFunction(functionBuilder.build()).build()
                .writeTo(KspContext.environment.codeGenerator, false)

        }
    }

}