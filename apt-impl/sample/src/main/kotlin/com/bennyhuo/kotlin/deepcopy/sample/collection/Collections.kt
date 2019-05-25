package com.bennyhuo.kotlin.deepcopy.sample.collection

import com.bennyhuo.kotlin.deepcopy.annotations.DeepCopy
import com.bennyhuo.kotlin.deepcopy.runtime.DeepCopyScope

@DeepCopy
data class Team(val name: String, val workers: List<Worker>)

@DeepCopy
data class Worker(val name: String)

fun main() {
    pressure()
}

fun test(){
    val map = mapOf(1 to mutableListOf(1, 2, 3), 2 to mutableListOf(3, 3, 2))
    DeepCopyScope.apply {
        val copied = map.deepCopy()
        println(map[1] !== copied[1])
        map.getValue(1) += 4
        println(map.getValue(1).size == 4)
        println(copied.getValue(1).size == 3)
    }
}

fun pressure(){
    println(cost {
        val t1 = Team("kotlin", List(1){ Worker("worker-$it") })
        val t2 = t1.deepCopy()
        println(t1.workers[0] === t2.workers[0])
    })

    val t3 = Team("kotlin", List(100000){ Worker("worker-$it") })
    repeat(10){
        println(cost { t3.deepCopy() })
    }
}

inline fun cost(block: ()->Unit): Long {
    val start = System.currentTimeMillis()
    block()
    return System.currentTimeMillis() - start
}