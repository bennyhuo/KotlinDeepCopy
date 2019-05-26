package com.bennyhuo.kotlin.deepcopy.compiler

import com.bennyhuo.aptutils.AptContext
import com.bennyhuo.aptutils.logger.Logger
import com.bennyhuo.aptutils.types.asKotlinTypeName
import com.bennyhuo.aptutils.types.isSubTypeOf
import com.bennyhuo.kotlin.deepcopy.annotations.DeepCopy
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.ParameterizedTypeName
import com.squareup.kotlinpoet.TypeName
import com.squareup.kotlinpoet.TypeVariableName
import kotlinx.metadata.jvm.KotlinClassMetadata
import java.util.*
import javax.lang.model.element.TypeElement

class KTypeElement private constructor(val typeElement: TypeElement, val kotlinClassName: TypeName, val enableDeepCopy: Boolean) : TypeElement by typeElement {

    companion object {
        private val refs = WeakHashMap<String, KTypeElement>()

        fun from(typeName: TypeName, enableDeepCopy: Boolean = false): KTypeElement? {
            val className = when(typeName){
                is ParameterizedTypeName -> typeName.rawType.canonicalName
                is ClassName -> typeName.canonicalName
                is TypeVariableName -> return null
                else -> throw IllegalArgumentException("Illegal type: $typeName")
            }
            val mappedCollectionName = kotlinCollectionTypeToJvmType[className]
            val name = mappedCollectionName ?: className
            return refs[typeName.toString()] ?: AptContext.elements.getTypeElement(name)?.let{
                KTypeElement(it, typeName, enableDeepCopy)
            }?.also { refs[typeName.toString()] = it }
        }

        fun from(typeElement: TypeElement, enableDeepCopy: Boolean = false): KTypeElement {
            val className = typeElement.qualifiedName.toString()
            return refs[className] ?: KTypeElement(typeElement, typeElement.asType().asKotlinTypeName(), enableDeepCopy).also { refs[className] = it }
        }
    }

    private val kClassMirror: KClassMirror? = getAnnotation(Metadata::class.java)?.let {
        it.parse() as? KotlinClassMetadata.Class
    }?.let(::KClassMirror)

    val isDataClass = kClassMirror?.isData ?: false

    val isCollectionType by lazy {
        typeElement.asType().isSubTypeOf("java.util.Collection")
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
        elementClassName?.let{ KTypeElement.from(it) }
    }

    val isDataType by lazy {
        isDataClass && (enableDeepCopy ||
                refs[when (kotlinClassName) {
                    is ParameterizedTypeName -> kotlinClassName.rawType.canonicalName
                    is ClassName -> kotlinClassName.canonicalName
                    else -> throw java.lang.IllegalArgumentException()
                }]?.enableDeepCopy == true)
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

    override fun toString(): String {
        return kotlinClassName.toString()
    }
}