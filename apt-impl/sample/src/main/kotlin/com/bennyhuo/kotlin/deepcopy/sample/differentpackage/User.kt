package com.bennyhuo.kotlin.deepcopy.sample.differentpackage

import com.bennyhuo.kotlin.deepcopy.annotations.DeepCopy
import com.bennyhuo.kotlin.deepcopy.sample.Project

@DeepCopy
data class User(val name: String, val project: Project)