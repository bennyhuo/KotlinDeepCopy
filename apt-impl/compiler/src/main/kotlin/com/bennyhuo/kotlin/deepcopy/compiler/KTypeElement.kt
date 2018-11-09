package com.bennyhuo.kotlin.deepcopy.compiler

import com.bennyhuo.aptutils.types.asKotlinTypeName
import com.bennyhuo.kotlin.deepcopy.annotations.DeepCopy
import kotlinx.metadata.jvm.KotlinClassMetadata
import javax.lang.model.element.TypeElement

class KTypeElement(typeElement: TypeElement) : TypeElement by typeElement {

    private val metaData: KClassMetadata? = getAnnotation(Metadata::class.java)?.let {
        it.parse() as? KotlinClassMetadata.Class
    }?.let(::KClassMetadata)

    val isDataClass = metaData?.isData ?: false

    val kotlinClassName = asType().asKotlinTypeName()

    val canDeepCopy = isDataClass && getAnnotation(DeepCopy::class.java) != null

    val components = metaData?.components ?: emptyList<KClassMetadata.Component>()

}