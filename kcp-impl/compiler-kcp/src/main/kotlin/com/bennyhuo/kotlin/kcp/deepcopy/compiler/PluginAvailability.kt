package com.bennyhuo.kotlin.kcp.deepcopy.compiler

import com.intellij.psi.PsiElement
import org.jetbrains.kotlin.descriptors.ClassDescriptor
import org.jetbrains.kotlin.ir.declarations.IrClass
import org.jetbrains.kotlin.resolve.source.PsiSourceElement

/**
 * Created by benny at 2022/1/13 2:15 PM.
 */
interface PluginAvailability {
    fun isAvailable(psiElement: PsiElement): Boolean = true

    fun ClassDescriptor.isDeepCopyPluginEnabled(): Boolean {
        val sourceElement = (source as? PsiSourceElement)?.psi ?: return false
        return isAvailable(sourceElement)
    }

    fun IrClass.isDeepCopyPluginEnabled(): Boolean {
        val sourceElement = (source as? PsiSourceElement)?.psi ?: return false
        return isAvailable(sourceElement)
    }
}