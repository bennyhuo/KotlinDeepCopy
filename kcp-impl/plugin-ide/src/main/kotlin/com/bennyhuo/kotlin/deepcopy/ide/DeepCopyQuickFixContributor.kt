package com.bennyhuo.kotlin.deepcopy.ide

import com.bennyhuo.kotlin.kcp.deepcopy.compiler.ErrorsDeepCopy
import org.jetbrains.kotlin.idea.quickfix.QuickFixContributor
import org.jetbrains.kotlin.idea.quickfix.QuickFixes

class DeepCopyQuickFixContributor : QuickFixContributor {
    override fun registerQuickFixes(quickFixes: QuickFixes) {
        quickFixes.register(
            ErrorsDeepCopy.ELEMENT_NOT_IMPLEMENT_DEEPCopyable,
            DeepCopyAddSupertypeQuickFix.Factory
        )
    }
}