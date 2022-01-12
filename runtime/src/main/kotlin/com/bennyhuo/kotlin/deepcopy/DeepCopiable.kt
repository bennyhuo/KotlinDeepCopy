package com.bennyhuo.kotlin.deepcopy

interface DeepCopiable<T> {
    fun deepCopy(): T
}