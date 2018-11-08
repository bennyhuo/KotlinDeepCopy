package com.bennyhuo.kotlin.deepcopy.compiler

import com.bennyhuo.aptutils.AptContext
import com.bennyhuo.kotlin.deepcopy.annotations.DeepCopy
import kotlinx.metadata.jvm.KotlinClassHeader
import javax.annotation.processing.*
import javax.lang.model.SourceVersion
import javax.lang.model.element.TypeElement

@SupportedAnnotationTypes("com.bennyhuo.kotlin.deepcopy.annotations.DeepCopy", "kotlin.Metadata")
@SupportedSourceVersion(SourceVersion.RELEASE_7)
class DeepCopyProcessor : AbstractProcessor() {

    override fun init(processingEnv: ProcessingEnvironment) {
        super.init(processingEnv)
        AptContext.init(processingEnv)
    }

    override fun process(annotations: MutableSet<out TypeElement>, roundEnv: RoundEnvironment): Boolean {
        roundEnv.getElementsAnnotatedWith(DeepCopy::class.java)
            .filter {
                val metaData = it.getAnnotation(Metadata::class.java)?: return@filter false
                KotlinClassHeader(metaData.kind,
                    metaData.metadataVersion,
                    metaData.bytecodeVersion, metaData.data1, metaData.data2, metaData.extraString, metaData.packageName, metaData.extraInt)
                true
            }
        return true
    }
}