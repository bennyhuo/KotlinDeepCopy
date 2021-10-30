package com.bennyhuo.kotlin.deepcopy.sample.collection

import com.bennyhuo.kotlin.deepcopy.annotations.DeepCopy
import com.bennyhuo.kotlin.deepcopy.runtime.DeepCopyScope
import com.bennyhuo.kotlin.deepcopy.sample.differentpackage.User

@DeepCopy
data class Team(val name: String, val workers: List<Worker>)

@DeepCopy
data class Team3(val name: String, val workers: List<User>)

@DeepCopy
data class Worker(val name: String)

@DeepCopy
data class Team2(val name: String, val workers: List<Worker2>, val pair: Pair<String, Worker2>, val map: Map<String, Worker2>){
    @DeepCopy
    data class Worker2(val name: String)
}


fun main() {
    testInnerClass()
}

fun testInnerClass(){
    val team = Team2("KotlinCN", listOf(Team2.Worker2("bennyhuo")), "hello" to Team2.Worker2("bennyhuo"), mapOf())
    val teamCopied = team.deepCopy()
    println(team.workers[0] === teamCopied.workers[0])
    println(team.pair === teamCopied.pair)
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