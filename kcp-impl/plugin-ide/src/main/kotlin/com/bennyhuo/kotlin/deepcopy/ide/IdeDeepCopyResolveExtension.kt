package com.bennyhuo.kotlin.deepcopy.ide

import com.bennyhuo.kotlin.kcp.deepcopy.compiler.DeepCopyResolveExtension
import com.intellij.psi.PsiElement

/**
 * Created by benny at 2022/1/13 5:09 PM.
 */
class IdeDeepCopyResolveExtension: DeepCopyResolveExtension() {

    override fun isAvailable(psiElement: PsiElement): Boolean {
        return DeepCopyAvailability.isAvailable(psiElement)
    }

}