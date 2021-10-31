package com.bennyhuo.kotlin.deepcopy.compiler

import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.ParameterSpec
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.squareup.kotlinpoet.TypeVariableName
import com.squareup.kotlinpoet.ksp.*

/**
 * Created by benny.
 */
class DeepCopyGenerator {

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
                .returns(dataClassName)
                .also { builder -> 
                    if (isKotlinJvm) builder.addAnnotation(JvmOverloads::class)
                }
                .addTypeVariables(dataClass.typeParameters.map {
                    it.toTypeVariableName(typeParameterResolver).let { TypeVariableName(it.name, it.bounds) }
                }).also { builder ->
                    dataClass.containingFile?.let { builder.addOriginatingKSFile(it) }
                }

            val statementStringBuilder = StringBuilder("%T(")

            dataClass.primaryConstructor!!.parameters.forEach { parameter ->
                val type = parameter.type.resolve()
                if (type.declaration.deepCopiable) {
                    fileSpecBuilder.addImport(type.declaration.escapedPackageName, "deepCopy")
                    
                    val nullableMark = if (type.isMarkedNullable) "?" else ""
                    statementStringBuilder.append("${parameter.name!!.asString()}${nullableMark}.deepCopy(), ")
                } else if (type.declaration.isSupportedCollectionType) {
                    val elementType = type.arguments.single().type!!.resolve().declaration
                    val method = if (elementType.deepCopiable) {
                        fileSpecBuilder.addImport(RUNTIME_PACKAGE, "deepCopy")
                        fileSpecBuilder.addImport(elementType.escapedPackageName, "deepCopy")
                        "deepCopy { it.deepCopy() }"
                    } else {
                        fileSpecBuilder.addImport(RUNTIME_PACKAGE, "copy")
                        "copy()"
                    }
                    val nullableMark = if (type.isMarkedNullable) "?" else ""
                    statementStringBuilder.append("${parameter.name!!.asString()}${nullableMark}.${method}, ")
                } else if (type.declaration.isSupportedMapType) {
                    val keyType = type.arguments[0].type!!.resolve().declaration
                    val valueType = type.arguments[1].type!!.resolve().declaration
                    val method = when {
                        keyType.deepCopiable && valueType.deepCopiable -> {
                            fileSpecBuilder.addImport(RUNTIME_PACKAGE, "deepCopy")
                            fileSpecBuilder.addImport(keyType.escapedPackageName, "deepCopy")
                            fileSpecBuilder.addImport(valueType.escapedPackageName, "deepCopy")
                            "deepCopy({ it.deepCopy() }, { it.deepCopy() })"
                        }
                        keyType.deepCopiable && !valueType.deepCopiable -> {
                            fileSpecBuilder.addImport(RUNTIME_PACKAGE, "deepCopy")
                            fileSpecBuilder.addImport(keyType.escapedPackageName, "deepCopy")
                            "deepCopy({ it.deepCopy() }, { it })"
                        }
                        !keyType.deepCopiable && valueType.deepCopiable -> {
                            fileSpecBuilder.addImport(RUNTIME_PACKAGE, "deepCopy")
                            fileSpecBuilder.addImport(valueType.escapedPackageName, "deepCopy")
                            "deepCopy({ it }, { it.deepCopy() })"   
                        }
                        else -> {
                            fileSpecBuilder.addImport(RUNTIME_PACKAGE, "copy")
                            "copy()"
                        }
                    }
                    val nullableMark = if (type.isMarkedNullable) "?" else ""
                    statementStringBuilder.append("${parameter.name!!.asString()}${nullableMark}.${method}, ")
                } else {
                    statementStringBuilder.append("${parameter.name!!.asString()}, ")
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
            )
            
            fileSpecBuilder.addFunction(functionBuilder.build()).build()
                .writeTo(KspContext.environment.codeGenerator, false)

            DeepCopyLoopDetector(dataClass).detect()
        }
    }

}