package com.bennyhuo.kotlin.deepcopy.compiler.apt.meta

import com.bennyhuo.aptutils.AptContext
import com.bennyhuo.aptutils.types.asKotlinTypeName
import com.bennyhuo.kotlin.deepcopy.annotations.DeepCopy
import com.bennyhuo.kotlin.deepcopy.compiler.apt.*
import com.bennyhuo.kotlin.deepcopy.compiler.apt.loop.DeepCopyLoopException
import com.squareup.kotlinpoet.*
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

    val isDeepCopiable = isDataClass && (typeElement.getAnnotation(DeepCopy::class.java) != null
            || this in Index)

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

fun KTypeElement?.isDeepCopiable(): Boolean {
    contract {
        returns(true) implies (this@isDeepCopiable != null)
    }
    return this?.isDeepCopiable == true
} 