package com.bennyhuo.kotlin.deepcopy.compiler.ksp.utils

import com.google.devtools.ksp.processing.SymbolProcessorEnvironment

/**
 * Created by benny.
 */
interface LoggerMixin {

    val env: SymbolProcessorEnvironment

    val logger
        get() = env.logger

}