package com.bennyhuo.kotlin.deepcopy.compiler

import com.bennyhuo.kotlin.deepcopy.annotations.DeepCopy
import com.google.devtools.ksp.isAnnotationPresent
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSDeclaration

inline fun escapeStdlibPackageName(packageName: String) =
    if (packageName == "kotlin") "com.bennyhuo.kotlin.deepcopy.builtin" else packageName


val KSDeclaration.escapedPackageName: String
    get() = escapeStdlibPackageName(packageName.asString()).also { 
        logger.warn("$this: ${packageName.asString()} -> $it")
    }

val KSDeclaration.canDeepCopy: Boolean
    get() = this is KSClassDeclaration && 
            (isAnnotationPresent(DeepCopy::class) || this in Index)