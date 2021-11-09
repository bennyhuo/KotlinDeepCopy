package com.bennyhuo.kotlin.deepcopy.library

import com.bennyhuo.kotlin.deepcopy.annotations.DeepCopy
import com.bennyhuo.kotlin.deepcopy.annotations.DeepCopyConfig

@DeepCopy
data class Hello<out P1, out P2>(
    val first: P1,
    val second: P2
)


@DeepCopyConfig(values = [Pair::class])
class Config