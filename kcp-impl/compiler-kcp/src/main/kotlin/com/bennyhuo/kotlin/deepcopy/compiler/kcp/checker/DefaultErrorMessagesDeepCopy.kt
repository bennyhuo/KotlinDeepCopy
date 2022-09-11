package com.bennyhuo.kotlin.deepcopy.compiler.kcp.checker

import org.jetbrains.kotlin.diagnostics.rendering.DefaultErrorMessages
import org.jetbrains.kotlin.diagnostics.rendering.DiagnosticFactoryToRendererMap
import org.jetbrains.kotlin.diagnostics.rendering.Renderers

object DefaultErrorMessagesDeepCopy : DefaultErrorMessages.Extension {
    private val rendererMap = DiagnosticFactoryToRendererMap("DeepCopy")
    override fun getMap() = rendererMap

    init {
        rendererMap.put(
            ErrorsDeepCopy.ELEMENT_NOT_IMPLEMENT_DEEPCOPYABLE,
            "''{0}'' should implement ''com.bennyhuo.kotlin.deepcopy.DeepCopyable<T>'' to support deep copy.",
            Renderers.TO_STRING
        )
    }
}
