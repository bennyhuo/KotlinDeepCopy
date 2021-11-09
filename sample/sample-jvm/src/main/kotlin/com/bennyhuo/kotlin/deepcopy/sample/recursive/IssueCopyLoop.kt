package com.bennyhuo.kotlin.deepcopy.sample.recursive

import com.bennyhuo.kotlin.deepcopy.annotations.DeepCopy

@DeepCopy
data class Hello(var name: String)

@DeepCopy
data class DataStartParam constructor(
    var text: String,
    var hello1: Hello? = null,
    var hello2: Hello? = null,
    var hello3: Hello? = null
)