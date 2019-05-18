package com.bennyhuo.kotlin.deepcopy.compiler

import com.bennyhuo.aptutils.AptContext
import com.bennyhuo.aptutils.logger.Logger
import com.bennyhuo.aptutils.types.asKotlinTypeName
import com.bennyhuo.kotlin.deepcopy.annotations.DeepCopy
import com.squareup.kotlinpoet.TypeName
import kotlinx.metadata.jvm.KotlinClassMetadata
import java.util.*
import javax.lang.model.element.TypeElement

class KTypeElement private constructor(typeElement: TypeElement) : TypeElement by typeElement {

    companion object{
        private val refs = WeakHashMap<String, KTypeElement>()

        fun from(className: String): KTypeElement? {
            return refs[className] ?: AptContext.elements.getTypeElement(className)?.let(::KTypeElement)?.also { refs[className] = it }
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

    val canDeepCopy = isDataClass && getAnnotation(DeepCopy::class.java) != null

    val components = kClassMirror?.components ?: emptyList<KClassMirror.Component>()

    val typeVariablesWithoutVariance = kClassMirror?.typeParameters?.map {
        it.typeVariableNameWithoutVariance
    } ?: emptyList()

    val typeVariables = kClassMirror?.typeParameters?.map {
        it.typeVariableName
    } ?: emptyList()

    private var marked: Boolean = false

    fun mark(){
        if(marked){
            throw CopyLoopException(this)
        } else {
            marked = true
        }
    }

    fun unmark(){
        marked = false
    }
}