package com.bennyhuo.kotlin.deepcopy.sample.recursive

import com.bennyhuo.kotlin.deepcopy.annotations.DeepCopy

/**
 * It will lead to an apt exception to uncomment this.
 * CopyLoopException: Detect infinite copy loop.
 * It will cause stack overflow to call com.bennyhuo.kotlin.deepcopy.sample.recursive.Owner.deepCopy() in the runtime.
 * */
//@DeepCopy
//data class Project(val name: String, var owner: Owner?)

/**
 * Maybe this is what you want.
 **/
@DeepCopy
data class Project(val name: String){
    lateinit var owner: Owner
}

@DeepCopy
data class Owner(val project: Project)
