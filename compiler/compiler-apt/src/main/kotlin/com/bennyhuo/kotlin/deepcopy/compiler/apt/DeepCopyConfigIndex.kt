package com.bennyhuo.kotlin.deepcopy.compiler.apt

import com.bennyhuo.aptutils.types.asElement
import com.bennyhuo.aptutils.types.asTypeMirror
import com.bennyhuo.kotlin.deepcopy.annotations.DeepCopyConfig
import javax.lang.model.element.Element
import javax.lang.model.element.TypeElement
import javax.lang.model.type.MirroredTypesException

class DeepCopyConfigIndex(configs: Collection<Element>?) {

    companion object {
        val instance: DeepCopyConfigIndex
            get() = currentInstance!!

        private var currentInstance: DeepCopyConfigIndex? = null

        operator fun contains(element: TypeElement): Boolean {
            return element.qualifiedName.toString() in instance.deepCopyClassNamesFromConfig
        }

        fun release() {
            currentInstance = null
        }
    }

    init {
        currentInstance = this
    }

    val deepCopyClassesFromConfig = configs?.asSequence()?.map {
        it.getAnnotation(DeepCopyConfig::class.java)
    }?.flatMap {
        try {
            it.values.map { it.asTypeMirror() }
        } catch (e: MirroredTypesException) {
            e.typeMirrors
        }
    }?.map { it.asElement() }
        ?.filterIsInstance<TypeElement>()
        ?.toSet() ?: emptySet()

    val deepCopyClassNamesFromConfig = deepCopyClassesFromConfig.mapTo(HashSet()) {
        it.qualifiedName.toString()
    }
}