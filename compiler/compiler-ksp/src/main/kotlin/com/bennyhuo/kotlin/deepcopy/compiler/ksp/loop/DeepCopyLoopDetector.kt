package com.bennyhuo.kotlin.deepcopy.compiler.ksp.loop

import com.bennyhuo.kotlin.deepcopy.compiler.ksp.deepCopyable
import com.bennyhuo.kotlin.deepcopy.compiler.ksp.utils.LoggerMixin
import com.google.devtools.ksp.processing.SymbolProcessorEnvironment
import com.google.devtools.ksp.symbol.KSClassDeclaration
import java.util.*

class DeepCopyLoopDetector(
    override val env: SymbolProcessorEnvironment,
    private val declaration: KSClassDeclaration
) : LoggerMixin {

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
                it.deepCopyable
            }.forEach {
                push(it)
                detect(it, false)
                pop()
            }
    }

    private fun push(declaration: KSClassDeclaration) {
        if (!typeSet.add(declaration)) {
            throw DeepCopyLoopException(declaration)
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