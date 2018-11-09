package com.bennyhuo.kotlin.deepcopy.compiler

import com.bennyhuo.aptutils.AptContext
import com.bennyhuo.aptutils.logger.Logger
import com.bennyhuo.aptutils.types.asKotlinTypeName
import com.bennyhuo.aptutils.types.packageName
import com.bennyhuo.aptutils.types.simpleName
import com.bennyhuo.kotlin.deepcopy.annotations.DeepCopy
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.KModifier
import kotlinx.metadata.ClassName
import kotlinx.metadata.Flags
import kotlinx.metadata.KmClassVisitor
import kotlinx.metadata.KmFunctionVisitor
import kotlinx.metadata.jvm.KotlinClassHeader
import kotlinx.metadata.jvm.KotlinClassMetadata
import java.io.IOException
import java.lang.StringBuilder
import javax.annotation.processing.*
import javax.lang.model.SourceVersion
import javax.lang.model.element.TypeElement
import javax.tools.StandardLocation

@SupportedAnnotationTypes("com.bennyhuo.kotlin.deepcopy.annotations.DeepCopy", "kotlin.Metadata")
@SupportedSourceVersion(SourceVersion.RELEASE_7)
class DeepCopyProcessor : AbstractProcessor() {

    override fun init(processingEnv: ProcessingEnvironment) {
        super.init(processingEnv)
        AptContext.init(processingEnv)
    }

    override fun process(annotations: MutableSet<out TypeElement>, roundEnv: RoundEnvironment): Boolean {
        roundEnv.getElementsAnnotatedWith(DeepCopy::class.java)
            .filter { it.kind.isClass }
            .mapNotNull { (it as? TypeElement)?.let(::KTypeElement) }
            .map {
                val fileSpecBuilder = FileSpec.builder(it.packageName(), it.simpleName())
                val functionBuilder = FunSpec.builder("deepCopy")
                    .receiver(it.kotlinClassName)
                    .addModifiers(KModifier.PUBLIC)
                    .returns(it.kotlinClassName)

                val statementStringBuilder = StringBuilder("%T(")
                val parameters = ArrayList<Any>()
                it.components.forEach { component ->
                    statementStringBuilder.append(" %L,")
                    if(component.typeElement?.canDeepCopy == true){
                        parameters.add("${component.name}.deepCopy()")
                    } else {
                        parameters.add(component.kotlinClassName)
                    }
                }
                statementStringBuilder.setCharAt(statementStringBuilder.lastIndex, ')')

                functionBuilder.addStatement("return $statementStringBuilder", it.kotlinClassName, *(parameters.toTypedArray()))

                writeKotlinToFile(fileSpecBuilder.addFunction(functionBuilder.build()).build())
            }
        return true
    }

    private fun writeKotlinToFile(fileSpec: FileSpec) {
        try {
            val fileObject = AptContext.filer.createResource(StandardLocation.SOURCE_OUTPUT, fileSpec.packageName, fileSpec.name + ".kt")
            val writer = fileObject.openWriter()
            fileSpec.writeTo(writer)
            writer.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

}