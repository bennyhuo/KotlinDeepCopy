package com.bennyhuo.kotlin.deepcopy.compiler

import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.processing.SymbolProcessorEnvironment
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSType
import com.google.devtools.ksp.symbol.Modifier
import com.squareup.kotlinpoet.*
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.squareup.kotlinpoet.ksp.*

/**
 * Created by benny at 2021/6/20 19:02.
 */
class DeepCopySymbolProcessor(private val environment: SymbolProcessorEnvironment) :
    SymbolProcessor {

    private val logger = environment.logger

    override fun process(resolver: Resolver): List<KSAnnotated> {
        try {
            logger.warn("DeepCopySymbolProcessor, ${KotlinVersion.CURRENT}")

            val deepCopyTypeFromConfigs =
                resolver.getSymbolsWithAnnotation("com.bennyhuo.kotlin.deepcopy.annotations.DeepCopyConfig")
                    .filterIsInstance<KSClassDeclaration>()
                    .flatMap {
                        it.annotations
                    }.flatMap {
                        it.arguments
                    }.flatMap {
                        when (val value = it.value) {
                            is List<*> -> value.asSequence()
                            else -> sequenceOf(value)
                        }
                    }.filterIsInstance<KSType>()
                    .map { it.declaration }
                    .filterIsInstance<KSClassDeclaration>()

            val deepCopyTypes =
                resolver.getSymbolsWithAnnotation("com.bennyhuo.kotlin.deepcopy.annotations.DeepCopy")
                    .filterIsInstance<KSClassDeclaration>()
                    .filter { Modifier.DATA in it.modifiers }
                    .toSet() + deepCopyTypeFromConfigs

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
                    dataClass.packageName.asString(),
                    "${dataClass.simpleName.asString()}$\$DeepCopy"
                )
                val functionBuilder = FunSpec.builder("deepCopy")
                    .receiver(dataClassName)
                    .addModifiers(KModifier.PUBLIC)
                    .returns(dataClassName)
                    .addAnnotation(JvmOverloads::class)
                    .addTypeVariables(dataClass.typeParameters.map {
                        it.toTypeVariableName(typeParameterResolver)
                    }).also { builder ->
                        dataClass.containingFile?.let { builder.addOriginatingKSFile(it) }
                    }

                val statementStringBuilder = StringBuilder("%T(")
                val parameters = ArrayList<Any>()

                dataClass.primaryConstructor!!.parameters.forEach { parameter ->
                    statementStringBuilder.append("%L, ")
                    logger.warn("modifiers of ${parameter.type.resolve().declaration}: ${parameter.type.resolve().declaration.modifiers}")
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
                        logger.warn(parameter.type.toString(), parameter)
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
                    .writeTo(environment.codeGenerator, false)

            }
        } catch (e: Exception) {
            logger.exception(e)
        }
        return emptyList()
    }
}