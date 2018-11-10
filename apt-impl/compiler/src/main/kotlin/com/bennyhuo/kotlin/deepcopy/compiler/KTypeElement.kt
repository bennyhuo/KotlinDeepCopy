package com.bennyhuo.kotlin.deepcopy.compiler

import com.bennyhuo.aptutils.types.asKotlinTypeName
import com.bennyhuo.kotlin.deepcopy.annotations.DeepCopy
import kotlinx.metadata.jvm.KotlinClassMetadata
import javax.lang.model.element.TypeElement

class KTypeElement(typeElement: TypeElement) : TypeElement by typeElement {

    private val kClassMirror: KClassMirror? = getAnnotation(Metadata::class.java)?.let {
        it.parse() as? KotlinClassMetadata.Class
    }?.let(::KClassMirror)

    val isDataClass = kClassMirror?.isData ?: false

    val kotlinClassName = asType().asKotlinTypeName()

    val canDeepCopy = isDataClass && getAnnotation(DeepCopy::class.java) != null

    val components = kClassMirror?.components ?: emptyList<KClassMirror.Component>()

}