package com.bennyhuo.kotlin.deepcopy.compiler.kcp.checker

import com.intellij.psi.PsiElement
import org.jetbrains.kotlin.diagnostics.DiagnosticFactory1
import org.jetbrains.kotlin.diagnostics.Errors
import org.jetbrains.kotlin.diagnostics.Severity

interface ErrorsDeepCopy {
    companion object {
        @JvmField
        val ELEMENT_NOT_IMPLEMENT_DEEPCOPYABLE = DiagnosticFactory1.create<PsiElement, String>(Severity.WARNING)

        init {
            Errors.Initializer.initializeFactoryNamesAndDefaultErrorMessages(
                ErrorsDeepCopy::class.java,
                DefaultErrorMessagesDeepCopy
            )
        }
    }
}