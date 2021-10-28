package com.bennyhuo.kotlin.deepcopy.compiler

inline fun escapeStdlibPackageName(packageName: String) =
    if (packageName == "kotlin") "com.bennyhuo.kotlin.deepcopy.builtin" else packageName




//val KSClassDeclaration.canDeepCopy: Boolean
//    get() = asStarProjectedType()