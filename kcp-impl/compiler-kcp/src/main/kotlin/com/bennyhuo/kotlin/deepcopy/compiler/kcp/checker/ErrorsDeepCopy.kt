package com.bennyhuo.kotlin.kcp.deepcopy.compiler;

import com.bennyhuo.kotlin.deepcopy.compiler.kcp.checker.DefaultErrorMessagesDeepCopy;
import com.intellij.psi.PsiElement;

import org.jetbrains.kotlin.diagnostics.DiagnosticFactory1;
import org.jetbrains.kotlin.diagnostics.Errors;

import static org.jetbrains.kotlin.diagnostics.Severity.WARNING;

public interface ErrorsDeepCopy {
    DiagnosticFactory1<PsiElement, String> ELEMENT_NOT_IMPLEMENT_DEEPCopyable = DiagnosticFactory1.create(WARNING);

    @SuppressWarnings("UnusedDeclaration")
    Object _initializer = new Object() {
        {
            Errors.Initializer.initializeFactoryNamesAndDefaultErrorMessages(ErrorsDeepCopy.class, DefaultErrorMessagesDeepCopy.INSTANCE);
        }
    };

}