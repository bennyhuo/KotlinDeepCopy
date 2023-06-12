package com.bennyhuo.kotlin.deepcopy.compiler.ksp.loop

import com.google.devtools.ksp.symbol.KSClassDeclaration

class DeepCopyLoopException(val declaration: KSClassDeclaration)
    : Exception("Detect infinite copy loop. It will cause stack overflow " +
        "to call ${declaration.qualifiedName!!.asString()}.deepCopy() in the runtime.")
