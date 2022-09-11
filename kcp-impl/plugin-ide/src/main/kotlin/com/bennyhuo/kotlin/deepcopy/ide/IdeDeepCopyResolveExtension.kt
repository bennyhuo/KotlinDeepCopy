package com.bennyhuo.kotlin.deepcopy.ide

import com.bennyhuo.kotlin.deepcopy.compiler.kcp.symbol.DeepCopyResolveExtension
import com.intellij.psi.PsiElement

/**
 * Created by benny at 2022/1/13 5:09 PM.
 */
class IdeDeepCopyResolveExtension: DeepCopyResolveExtension() {

    override fun PsiElement.isDeepCopyPluginEnabled(): Boolean {
        return DeepCopyAvailability.isAvailable(this)
    }

}