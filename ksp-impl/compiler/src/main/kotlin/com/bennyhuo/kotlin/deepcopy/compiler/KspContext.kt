package com.bennyhuo.kotlin.deepcopy.compiler

import com.google.devtools.ksp.processing.KSBuiltIns
import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.processing.SymbolProcessorEnvironment

object KspContext {

    lateinit var environment: SymbolProcessorEnvironment
    lateinit var resolver: Resolver

    val builtIns: KSBuiltIns
        get() = resolver.builtIns

}

val logger: KSPLogger
    get() = KspContext.environment.logger