package com.bennyhuo.kotlin.deepcopy.compiler.ksp

import com.bennyhuo.kotlin.deepcopy.compiler.ksp.adapter.Adapter
import com.bennyhuo.kotlin.deepcopy.compiler.ksp.loop.DeepCopyLoopDetector
import com.bennyhuo.kotlin.deepcopy.compiler.ksp.meta.KComponent
import com.bennyhuo.kotlin.deepcopy.compiler.ksp.utils.Platform
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.processing.SymbolProcessorEnvironment
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.squareup.kotlinpoet.CodeBlock
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.LambdaTypeName
import com.squareup.kotlinpoet.ParameterSpec
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.squareup.kotlinpoet.TypeVariableName
import com.squareup.kotlinpoet.asTypeName
import com.squareup.kotlinpoet.ksp.addOriginatingKSFile
import com.squareup.kotlinpoet.ksp.toClassName
import com.squareup.kotlinpoet.ksp.toTypeName
import com.squareup.kotlinpoet.ksp.toTypeParameterResolver
import com.squareup.kotlinpoet.ksp.toTypeVariableName
import com.squareup.kotlinpoet.ksp.writeTo

/**
 * Created by benny.
 */
class DeepCopyGenerator(
    val env: SymbolProcessorEnvironment,
) {

    fun generate(resolver: Resolver, deepCopyTypes: Set<KSClassDeclaration>) {
        val platform = Platform(env)

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
                "${dataClass.simpleName.asString()}$\$DeepCopy",
            )
            val functionBuilder = FunSpec.builder("deepCopy")
                .receiver(dataClassName)
                .returns(dataClassName)
                .also { builder ->
                    if (platform.isKotlinJvm) builder.addAnnotation(JvmOverloads::class)
                }
                .addTypeVariables(dataClass.typeParameters.map {
                    it.toTypeVariableName(typeParameterResolver).let { TypeVariableName(it.name, it.bounds) }
                }).also { builder ->
                    dataClass.containingFile?.let { builder.addOriginatingKSFile(it) }
                }

            val dslCopyCodeStringBuilder = StringBuilder()

            val dslFunctionBuilder = FunSpec.builder("deepCopy")
                .receiver(dataClassName)
                .returns(dataClassName)
                .addTypeVariables(dataClass.typeParameters.map {
                    it.toTypeVariableName(typeParameterResolver).let { TypeVariableName(it.name, it.bounds) }
                }).also { builder ->
                    dataClass.containingFile?.let { builder.addOriginatingKSFile(it) }
                }
                .addParameter(
                    ParameterSpec.builder(
                        "dslMethod",
                        LambdaTypeName.get(
                            receiver = dataClassName,
                            returnType = Unit::class.asTypeName(),
                        ),
                    ).build(),
                )
            val statementStringBuilder = StringBuilder("%T(")

            dataClass.primaryConstructor!!.parameters.forEach { parameter ->
                val adapter = Adapter(
                    KComponent(
                        parameter,
                        parameter.type.toTypeName(typeParameterResolver),
                    ),
                )
                adapter.addImport(fileSpecBuilder)
                adapter.addStatement(statementStringBuilder)

                functionBuilder.addParameter(
                    ParameterSpec.builder(
                        parameter.name!!.asString(),
                        parameter.type.toTypeName(typeParameterResolver)
                    ).defaultValue("this.${parameter.name!!.asString()}").build()
                )

                dslCopyCodeStringBuilder.append("${parameter.name!!.asString()},")
            }

            statementStringBuilder.setCharAt(statementStringBuilder.lastIndex - 1, ')')
            functionBuilder.addStatement(
                "return $statementStringBuilder",
                dataClassName,
            )

            // link dslFunction
            dslFunctionBuilder
                .addCode(
                    CodeBlock.builder()
                        .addStatement("val copy = this.copy()")
                        .addStatement("copy.dslMethod()")
                        .addStatement("return copy.deepCopy()")
                        .build(),
                )

            fileSpecBuilder
                .addFunction(functionBuilder.build())
                .addFunction(dslFunctionBuilder.build())
                .build()
                .writeTo(env.codeGenerator, false)

            DeepCopyLoopDetector(env, dataClass).detect()
        }
    }
}
