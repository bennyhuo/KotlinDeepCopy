package com.bennyhuo.kotlin.deepcopy.compiler.ksp

import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSDeclaration
import com.google.devtools.ksp.symbol.KSType

class DeepCopyConfigIndex(configs: Collection<KSAnnotated>?) {

    companion object {
        val instance: DeepCopyConfigIndex
            get() = currentInstance!!

        private var currentInstance: DeepCopyConfigIndex? = null

        operator fun contains(declaration: KSDeclaration): Boolean {
            return declaration in instance.deepCopyClassDeclarations
        }

        fun release() {
            currentInstance = null
        }
    }

    init {
        currentInstance = this
    }

    val deepCopyClassDeclarations = configs?.filterIsInstance<KSClassDeclaration>()
        ?.flatMap {
            it.annotations
        }?.flatMap {
            it.arguments
        }?.flatMap {
            when (val value = it.value) {
                is List<*> -> value.asSequence()
                else -> sequenceOf(value)
            }
        }?.filterIsInstance<KSType>()
        ?.map { it.declaration }
        ?.filterIsInstance<KSClassDeclaration>()
        ?.toSet()
        ?: emptySet()
}