package com.bennyhuo.kotlin.deepcopy.sample.nullable

import com.bennyhuo.kotlin.deepcopy.annotations.DeepCopy

@DeepCopy
data class User(val name: String)

@DeepCopy
data class Nullables(val user: User?, val list: List<User>?)