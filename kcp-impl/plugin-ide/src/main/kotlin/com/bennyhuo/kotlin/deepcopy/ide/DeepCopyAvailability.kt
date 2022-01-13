package com.bennyhuo.kotlin.deepcopy.ide

import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.extensions.ExtensionPointName
import com.intellij.openapi.module.Module
import com.intellij.psi.PsiElement
import org.jetbrains.kotlin.idea.compilerPlugin.parcelize.ParcelizeAvailabilityProvider
import org.jetbrains.kotlin.idea.util.module

/**
 * Created by benny at 2022/1/13 1:04 PM.
 */
object DeepCopyAvailability {
    fun isAvailable(element: PsiElement): Boolean {
        if (ApplicationManager.getApplication().isUnitTestMode) {
            return true
        }

        val module = element.module ?: return false
        return isAvailable(module)
    }

    fun isAvailable(module: Module): Boolean {
        if (ApplicationManager.getApplication().isUnitTestMode) {
            return true
        }

        return DeepCopyAvailabilityProvider.PROVIDER_EP.getExtensions(module.project).any { it.isAvailable(module) }
    }
}

interface DeepCopyAvailabilityProvider {
    companion object {
        val PROVIDER_EP: ExtensionPointName<DeepCopyAvailabilityProvider> =
            ExtensionPointName("com.bennyhuo.kotlin.deepcopy.ide.availabilityProvider")
    }

    fun isAvailable(module: Module): Boolean
}