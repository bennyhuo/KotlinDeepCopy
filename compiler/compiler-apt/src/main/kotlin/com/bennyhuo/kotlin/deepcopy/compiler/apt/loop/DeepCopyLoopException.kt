package com.bennyhuo.kotlin.deepcopy.compiler.apt.loop

import com.bennyhuo.kotlin.deepcopy.compiler.apt.KTypeElement

class DeepCopyLoopException(kTypeElement: KTypeElement)
    : Exception("Detect infinite copy loop. It will cause stack overflow to call ${kTypeElement.kotlinClassName}.deepCopy() in the runtime.") {
}