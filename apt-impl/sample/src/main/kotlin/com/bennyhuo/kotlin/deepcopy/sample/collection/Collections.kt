package com.bennyhuo.kotlin.deepcopy.sample.collection

import com.bennyhuo.kotlin.deepcopy.annotations.DeepCopy

@DeepCopy
data class Team(val name: String, val workers: ArrayList<Worker>)

@DeepCopy
data class Worker(val name: String)

fun main() {
    val t1 = Team("kotlin", arrayListOf(Worker("benny"), Worker("danny")))
    val t2 = t1.deepCopy()
    println(t1.workers[1] === t2.workers[1])
}