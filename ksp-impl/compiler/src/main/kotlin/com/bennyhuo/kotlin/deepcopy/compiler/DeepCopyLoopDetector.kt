//package com.bennyhuo.kotlin.deepcopy.compiler
//
//import com.google.devtools.ksp.symbol.KSClassDeclaration
//import java.util.*
//import kotlin.collections.HashSet
//
//class DeepCopyLoopDetector(private val kTypeElement: KSClassDeclaration) {
//
//    private val typeStack = Stack<KSClassDeclaration>()
//    private val typeSet = HashSet<KSClassDeclaration>()
//
//    fun detect() {
//        push(kTypeElement)
//        kTypeElement.primaryConstructor!!.parameters
//            // Only nullable types should be checked.
//            .filter { it.type.resolve().isMarkedNullable }
//            .filter { it.canDeepCopy }
//            .forEach {
//                push(it)
//                detectNext(it)
//                pop()
//            }
//        pop()
//    }
//
//    private fun detectNext(kTypeElement: KSClassDeclaration) {
//        kTypeElement.primaryConstructor!!.parameters.mapNotNull { it.typeElement }
//            .filter { it.canDeepCopy }
//            .forEach {
//                push(it)
//                detectNext(it)
//                pop()
//            }
//    }
//
//    private fun push(kTypeElement: KTypeElement) {
//        kTypeElement.mark()
//        typeStack.push(kTypeElement)
//    }
//
//    private fun pop() {
//        typeStack.pop()?.unmark()
//    }
//
//    private fun dumpStack() {
//        // logger.warn("${kTypeElement.qualifiedName}: [${typeStack.joinToString { it.simpleName }}]")
//    }
//}