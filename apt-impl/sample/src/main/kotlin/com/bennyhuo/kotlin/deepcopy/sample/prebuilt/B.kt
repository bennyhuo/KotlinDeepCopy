package com.bennyhuo.kotlin.deepcopy.sample.prebuilt

import com.bennyhuo.kotlin.deepcopy.annotations.DeepCopy

/**
 * Created by benny.
 */
@DeepCopy
data class B(
    val pair: Pair<String, Any>,
    val triple: Triple<String, Int, Any>,
    val hello: Hello<Int, Double>
)