package com.bennyhuo.kotlin.deepcopy.sample.collection

import com.bennyhuo.kotlin.deepcopy.annotations.DeepCopy

@DeepCopy
data class Team(val name: String, val workers: List<Worker>)

@DeepCopy
data class Worker(val name: String)