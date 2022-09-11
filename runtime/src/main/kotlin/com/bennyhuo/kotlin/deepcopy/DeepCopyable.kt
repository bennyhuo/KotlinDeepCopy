package com.bennyhuo.kotlin.deepcopy

interface DeepCopyable<T> {
    fun deepCopy(): T
}