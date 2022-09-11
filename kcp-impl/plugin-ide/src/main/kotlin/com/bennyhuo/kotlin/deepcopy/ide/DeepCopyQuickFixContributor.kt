package com.bennyhuo.kotlin.deepcopy.ide

import com.bennyhuo.kotlin.deepcopy.compiler.kcp.checker.ErrorsDeepCopy
import org.jetbrains.kotlin.idea.quickfix.QuickFixContributor
import org.jetbrains.kotlin.idea.quickfix.QuickFixes

class DeepCopyQuickFixContributor : QuickFixContributor {
    override fun registerQuickFixes(quickFixes: QuickFixes) {
        quickFixes.register(
            ErrorsDeepCopy.ELEMENT_NOT_IMPLEMENT_DEEPCOPYABLE,
            DeepCopyAddSupertypeQuickFix.Factory
        )
    }
}