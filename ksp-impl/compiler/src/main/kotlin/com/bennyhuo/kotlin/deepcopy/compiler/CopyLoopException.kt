package com.bennyhuo.kotlin.deepcopy.compiler

import com.google.devtools.ksp.symbol.KSClassDeclaration

class CopyLoopException(declaration: KSClassDeclaration)
    : Exception("""
        |Detect infinite copy loop. 
        |It will cause stack overflow to call ${declaration.qualifiedName!!.asString()}.deepCopy() in the runtime.
        |""".trimMargin())