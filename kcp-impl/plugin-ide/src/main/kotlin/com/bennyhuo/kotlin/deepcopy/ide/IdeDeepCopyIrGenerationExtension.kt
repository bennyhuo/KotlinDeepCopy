package com.bennyhuo.kotlin.deepcopy.ide

import com.bennyhuo.kotlin.deepcopy.compiler.kcp.ir.DeepCopyIrGenerationExtension
import com.intellij.psi.PsiElement

/**
 * Created by benny at 2022/1/14 12:12 AM.
 */
class IdeDeepCopyIrGenerationExtension : DeepCopyIrGenerationExtension() {
    override fun PsiElement.isDeepCopyPluginEnabled(): Boolean {
        return DeepCopyAvailability.isAvailable(this)
    }
}