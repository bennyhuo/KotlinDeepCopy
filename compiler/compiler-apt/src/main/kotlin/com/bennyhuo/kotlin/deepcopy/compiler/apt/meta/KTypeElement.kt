package com.bennyhuo.kotlin.deepcopy.compiler.apt.meta

import com.bennyhuo.aptutils.AptContext
import com.bennyhuo.aptutils.types.asKotlinTypeName
import com.bennyhuo.kotlin.deepcopy.annotations.DeepCopy
import com.bennyhuo.kotlin.deepcopy.compiler.apt.DeepCopyConfigIndex
import com.bennyhuo.kotlin.deepcopy.compiler.apt.loop.DeepCopyLoopException
import com.bennyhuo.kotlin.deepcopy.compiler.apt.utils.isSupportedCollectionType
import com.bennyhuo.kotlin.deepcopy.compiler.apt.utils.isSupportedMapType
import com.bennyhuo.kotlin.deepcopy.compiler.apt.utils.kotlinCollectionTypeToJvmType
import com.bennyhuo.kotlin.deepcopy.compiler.apt.utils.parse
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.ParameterizedTypeName
import com.squareup.kotlinpoet.TypeName
import com.squareup.kotlinpoet.TypeVariableName
import com.squareup.kotlinpoet.WildcardTypeName
import kotlinx.metadata.jvm.KotlinClassMetadata
import java.util.*
import javax.lang.model.element.TypeElement
import kotlin.contracts.contract

class KTypeElement private constructor(
    val typeElement: TypeElement,
    val kotlinClassName: TypeName
) : TypeElement by typeElement {

    companion object {
        private val refs = WeakHashMap<String, KTypeElement>()

        fun from(typeName: TypeName): KTypeElement? {
            val className = when (typeName) {
                is ParameterizedTypeName -> typeName.rawType.canonicalName
                is ClassName -> typeName.canonicalName
                is TypeVariableName,
                is WildcardTypeName -> return null
                else -> throw IllegalArgumentException("Illegal type: $typeName")
            }
            val mappedCollectionName = kotlinCollectionTypeToJvmType[className]
            val name = mappedCollectionName ?: className
            return refs[typeName.toString()] ?: AptContext.elements.getTypeElement(name)?.let {
                KTypeElement(it, typeName)
            }?.also { refs[typeName.toString()] = it }
        }

        fun from(typeElement: TypeElement): KTypeElement {
            val className = typeElement.qualifiedName.toString()
            return refs[className] ?: KTypeElement(
                typeElement,
                typeElement.asType().asKotlinTypeName()
            ).also { refs[className] = it }
        }
    }

    private val kClassMeta: KClassMeta? = getAnnotation(Metadata::class.java)?.let {
        it.parse() as? KotlinClassMetadata.Class
    }?.let(::KClassMeta)

    val isDataClass = kClassMeta?.isData ?: false

    val isCollectionType by lazy {
        typeElement.isSupportedCollectionType
    }

    val isMapType by lazy {
        typeElement.isSupportedMapType
    }

    val isDeepCopyable = isDataClass && (typeElement.getAnnotation(DeepCopy::class.java) != null
            || typeElement in DeepCopyConfigIndex)

    val components = kClassMeta?.components ?: emptyList()

    val typeVariablesWithoutVariance = kClassMeta?.typeParameters?.map {
        it.typeVariableNameWithoutVariance
    } ?: emptyList()

    val typeVariables = kClassMeta?.typeParameters?.map {
        it.typeVariableName
    } ?: emptyList()

    private var marked: Boolean = false

    fun mark() {
        if (marked) {
            throw DeepCopyLoopException(this)
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

fun KTypeElement?.isDeepCopyable(): Boolean {
    contract {
        returns(true) implies (this@isDeepCopyable != null)
    }
    return this?.isDeepCopyable == true
} 