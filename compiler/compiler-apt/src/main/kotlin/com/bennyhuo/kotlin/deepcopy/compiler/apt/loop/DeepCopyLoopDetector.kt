package com.bennyhuo.kotlin.deepcopy.compiler.apt.loop

import com.bennyhuo.aptutils.logger.Logger
import com.bennyhuo.kotlin.deepcopy.compiler.apt.KTypeElement
import java.util.*

class DeepCopyLoopDetector(private val kTypeElement: KTypeElement) {

    private val typeStack = Stack<KTypeElement>()

    fun detect() {
        push(kTypeElement)
        kTypeElement.components
            // Only nullable types should be checked.
            .filter { it.type.isNullable }
            .mapNotNull { it.typeElement }
            .filter { it.canDeepCopy }
            .forEach {
                push(it)
                detectNext(it)
                pop()
            }
        pop()
    }

    private fun detectNext(kTypeElement: KTypeElement) {
        kTypeElement.components.mapNotNull { it.typeElement }
            .filter { it.canDeepCopy }
            .forEach {
                push(it)
                detectNext(it)
                pop()
            }
    }

    private fun push(kTypeElement: KTypeElement) {
        kTypeElement.mark()
        typeStack.push(kTypeElement)
    }

    private fun pop() {
        typeStack.pop()?.unmark()
    }

    private fun dumpStack() {
        Logger.warn("${kTypeElement.qualifiedName}: [${typeStack.joinToString { it.simpleName }}]")
    }
}