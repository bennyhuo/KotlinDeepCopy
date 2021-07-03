package com.bennyhuo.kotlin.deepcopy.compiler

import com.google.devtools.ksp.processing.Dependencies
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.processing.SymbolProcessorEnvironment
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.Modifier
import com.squareup.kotlinpoet.*

/**
 * Created by benny at 2021/6/20 19:02.
 */
class DeepCopySymbolProcessor(private val environment: SymbolProcessorEnvironment) :
    SymbolProcessor {

    private val logger = environment.logger

    override fun process(resolver: Resolver): List<KSAnnotated> {
        try {
            logger.warn("DeepCopySymbolProcessor, ${KotlinVersion.CURRENT}")
            val deepCopyTypes =
                resolver.getSymbolsWithAnnotation("com.bennyhuo.kotlin.deepcopy.annotations.DeepCopy")
                    .onEach {
                        logger.warn("DeepCopySymbolProcessor: $it")
                    }
                    .filterIsInstance<KSClassDeclaration>()
                    .onEach {
                        logger.warn("KSClassDeclaration: $it")
                    }
                    .filter { Modifier.DATA in it.modifiers }
                    .toSet()

            deepCopyTypes.forEach { dataClass: KSClassDeclaration ->
                logger.warn(dataClass.toString(), dataClass)
                logger.warn(dataClass.primaryConstructor!!.parameters.joinToString { "${it.name!!.getShortName()}: ${it.type}" })

                environment.codeGenerator.createNewFile(
                    Dependencies(true, dataClass.containingFile!!),
                    dataClass.packageName.asString(),
                    "${dataClass.simpleName.asString()}$\$DeepCopy"
                ).bufferedWriter().use { writer ->
                    val dataClassName =
                        ClassName(dataClass.packageName.asString(), dataClass.simpleName.asString())
                    val fileSpecBuilder = FileSpec.builder(
                        dataClass.packageName.asString(),
                        "${dataClass.simpleName.asString()}$\$DeepCopy"
                    )
                    val functionBuilder = FunSpec.builder("deepCopy")
                        .receiver(dataClassName)
                        .addModifiers(KModifier.PUBLIC)
                        .returns(dataClassName)
                    val statementStringBuilder = StringBuilder("%T(")
                    val parameters = ArrayList<Any>()

                    dataClass.primaryConstructor!!.parameters.forEach { parameter ->
                        statementStringBuilder.append("%L, ")
                        logger.warn("modifiers of ${parameter.type.resolve().declaration}: ${parameter.type.resolve().declaration.modifiers}")
                        if (Modifier.DATA in parameter.type.resolve().declaration.modifiers && parameter.type.resolve().declaration in deepCopyTypes) {
                            val deepCopyMethod =
                                MemberName(dataClass.packageName.asString(), "deepCopy")
                            if (parameter.type.resolve().isMarkedNullable) {
                                parameters.add(CodeBlock.of("${parameter.name!!.asString()}?.%M()", deepCopyMethod))
                            } else {
                                parameters.add(CodeBlock.of("${parameter.name!!.asString()}.%M()", deepCopyMethod))
                            }
                        } else {
                            parameters.add(parameter.name!!.asString())
                            logger.warn(parameter.type.toString(), parameter)
                        }

                        val parameterType = parameter.type.resolve().declaration as KSClassDeclaration
                        functionBuilder.addParameter(
                            ParameterSpec.builder(
                                parameter.name!!.asString(),
                                ClassName(
                                    parameterType.packageName.asString(),
                                    parameterType.simpleName.asString()
                                )
                            ).defaultValue("this.${parameter.name!!.asString()}").build()
                        )
                    }

                    statementStringBuilder.setCharAt(statementStringBuilder.lastIndex - 1, ')')
                    functionBuilder.addStatement("return $statementStringBuilder", dataClassName, *(parameters.toTypedArray()))
                    fileSpecBuilder.addFunction(functionBuilder.build()).build()
                        .writeTo(writer)
                }
            }
        } catch (e: Exception) {
            logger.exception(e)
        }
        return emptyList()
    }
}