package com.bennyhuo.kotlin.deepcopy.ide

import com.bennyhuo.kotlin.kcp.deepcopy.compiler.DeepCopyIrGenerationExtension
import com.intellij.psi.PsiElement

/**
 * Created by benny at 2022/1/14 12:12 AM.
 */
class IdeDeepCopyIrGenerationExtension : DeepCopyIrGenerationExtension() {
    override fun isAvailable(psiElement: PsiElement): Boolean {
        return DeepCopyAvailability.isAvailable(psiElement)
    }
}