package com.bennyhuo.kotlin.deepcopy.compiler

import com.bennyhuo.aptutils.AptContext
import com.bennyhuo.aptutils.logger.Logger
import com.bennyhuo.aptutils.types.asKotlinTypeName
import com.bennyhuo.aptutils.types.asTypeMirror
import com.bennyhuo.aptutils.types.erasure
import com.bennyhuo.aptutils.types.isSubTypeOf
import com.bennyhuo.kotlin.deepcopy.annotations.DeepCopy
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.ParameterizedTypeName
import kotlinx.metadata.jvm.KotlinClassMetadata
import java.util.*
import javax.lang.model.element.TypeElement

class KTypeElement private constructor(typeElement: TypeElement) : TypeElement by typeElement {

    companion object {
        private val refs = WeakHashMap<String, KTypeElement>()

        fun from(className: String): KTypeElement? {
            val mappedCollectionName = kotlinCollectionTypeToJvmType[className]
            val name = mappedCollectionName ?: className
            return refs[name] ?: AptContext.elements.getTypeElement(name)?.let(::KTypeElement)?.also { refs[name] = it }
        }

        fun from(typeElement: TypeElement): KTypeElement {
            val className = typeElement.qualifiedName.toString()
            return refs[className] ?: KTypeElement(typeElement).also { refs[className] = it }
        }
    }

    private val kClassMirror: KClassMirror? = getAnnotation(Metadata::class.java)?.let {
        it.parse() as? KotlinClassMetadata.Class
    }?.let(::KClassMirror)

    val isDataClass = kClassMirror?.isData ?: false

    val kotlinClassName = asType().asKotlinTypeName()

    val isCollectionType by lazy {
        typeElement.asType().erasure().isSubTypeOf("java.util.Collection")
    }

    val isMapType by lazy {
        typeElement.asType().isSubTypeOf("java.util.Map")
    }

    val elementClassName by lazy {
        when {
            isCollectionType -> (kotlinClassName as ParameterizedTypeName).typeArguments[0]
            isMapType -> (kotlinClassName as ParameterizedTypeName).typeArguments[1]
            else -> null
        } as? ClassName
    }

    val elementType by lazy {
        elementClassName?.canonicalName?.let { KTypeElement.from(it) }
    }

    val isDataType by lazy {
        isDataClass && getAnnotation(DeepCopy::class.java) != null
    }

    val canDeepCopy = isDataType || isCollectionType || isMapType

    val components = kClassMirror?.components ?: emptyList<KClassMirror.Component>()

    val typeVariablesWithoutVariance = kClassMirror?.typeParameters?.map {
        it.typeVariableNameWithoutVariance
    } ?: emptyList()

    val typeVariables = kClassMirror?.typeParameters?.map {
        it.typeVariableName
    } ?: emptyList()

    private var marked: Boolean = false

    fun mark() {
        if (marked) {
            throw CopyLoopException(this)
        } else {
            marked = true
        }
    }

    fun unmark() {
        marked = false
    }
}