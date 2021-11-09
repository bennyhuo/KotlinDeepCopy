package com.bennyhuo.kotlin.deepcopy.compiler.ksp

import com.google.devtools.ksp.symbol.KSClassDeclaration
import java.util.*

class DeepCopyLoopDetector(private val declaration: KSClassDeclaration) {

    private val typeStack = Stack<KSClassDeclaration>()
    private val typeSet = HashSet<KSClassDeclaration>()

    fun detect() {
        push(declaration)
        detect(declaration, true)
        pop()
    }

    private fun detect(declaration: KSClassDeclaration, checkNullable: Boolean) {
        declaration.primaryConstructor!!.parameters
            .map {
                it.type.resolve()
            }.let {
                if (checkNullable) {
                    it.filter {
                        // Only nullable types should be checked for top level.
                        it.isMarkedNullable
                    }
                } else it
            }.mapNotNull {
                it.declaration as? KSClassDeclaration
            }.filter {
                it.deepCopiable
            }.forEach {
                push(it)
                detect(it, false)
                pop()
            }
    }

    private fun push(declaration: KSClassDeclaration) {
        if (!typeSet.add(declaration)) {
            throw CopyLoopException(declaration)
        }
        typeStack.push(declaration)
    }

    private fun pop() {
        typeSet.remove(typeStack.pop())
    }

    private fun dumpStack() {
        logger.warn("${declaration.qualifiedName!!.asString()}: [${typeStack.joinToString { it.simpleName.asString() }}]")
    }
}