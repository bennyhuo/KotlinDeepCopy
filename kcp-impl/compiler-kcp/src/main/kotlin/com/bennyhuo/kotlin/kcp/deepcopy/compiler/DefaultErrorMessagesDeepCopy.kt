package com.bennyhuo.kotlin.kcp.deepcopy.compiler

import org.jetbrains.kotlin.diagnostics.rendering.DefaultErrorMessages
import org.jetbrains.kotlin.diagnostics.rendering.DiagnosticFactoryToRendererMap
import org.jetbrains.kotlin.diagnostics.rendering.Renderers

object DefaultErrorMessagesDeepCopy : DefaultErrorMessages.Extension {
    private val MAP = DiagnosticFactoryToRendererMap("DeepCopy")
    override fun getMap() = MAP

    init {
        MAP.put(
            ErrorsDeepCopy.ELEMENT_NOT_IMPLEMENT_DEEPCOPIABLE,
            "''{0}'' should implement ''com.bennyhuo.kotlin.deepcopy.DeepCopiable<T>'' to support deep copy.",
            Renderers.TO_STRING
        )
    }
}
