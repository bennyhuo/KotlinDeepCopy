package com.bennyhuo.kotlin.deepcopy.sample.prebuilt

import com.bennyhuo.kotlin.deepcopy.annotations.DeepCopy
import com.bennyhuo.kotlin.deepcopy.sample.nullable.User

/**
 * Created by benny.
 */
@DeepCopy
data class B(
    val pair: Pair<String, Any>,
    val triple: Triple<String, Int, Any>,
    val user: User
)