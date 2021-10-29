package com.bennyhuo.kotlin.deepcopy.compiler

import com.bennyhuo.kotlin.deepcopy.annotations.DeepCopy
import com.google.devtools.ksp.isAnnotationPresent
import com.google.devtools.ksp.symbol.KSClassDeclaration

inline fun escapeStdlibPackageName(packageName: String) =
    if (packageName == "kotlin") "com.bennyhuo.kotlin.deepcopy.builtin" else packageName


val KSClassDeclaration.canDeepCopy: Boolean
    get() = isAnnotationPresent(DeepCopy::class) || this in IndexCollector.typesFromIndex