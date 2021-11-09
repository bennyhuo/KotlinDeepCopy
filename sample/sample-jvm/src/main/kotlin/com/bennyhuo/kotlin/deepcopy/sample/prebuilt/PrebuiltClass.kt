package com.bennyhuo.kotlin.deepcopy.sample.prebuilt

import com.bennyhuo.kotlin.deepcopy.annotations.DeepCopy
import com.bennyhuo.kotlin.deepcopy.annotations.DeepCopyConfig

@DeepCopy
data class Hello<out P1, out P2>(
    val first: P1,
    val second: P2
)


@DeepCopyConfig(values = [Triple::class])
class Config

@DeepCopy
data class A(
    val pair: Pair<String, Any>,
    var triple: Triple<String, Int, Any>,
    val hello: Hello<Int, Double>
)