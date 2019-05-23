package com.bennyhuo.kotlin.deepcopy.sample.collection

import com.bennyhuo.kotlin.deepcopy.annotations.DeepCopy
import com.bennyhuo.kotlin.deepcopy.runtime.DeepCopyScope

@DeepCopy
data class Team(val name: String, val workers: ArrayList<Worker>)

@DeepCopy
data class Worker(val name: String)

fun main() {
    val t1 = Team("kotlin", arrayListOf(Worker("benny"), Worker("danny")))
    val t2 = t1.deepCopy()
    println(t1.workers[1] === t2.workers[1])

    val map = mapOf(1 to mutableListOf(1, 2, 3), 2 to mutableListOf(3, 3, 2))
    DeepCopyScope.apply {
        val copied = map.deepCopy()
        println(map[1] !== copied[1])
        map.getValue(1) += 4
        println(map.getValue(1).size == 4)
        println(copied.getValue(1).size == 3)
    }
}