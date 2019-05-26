package com.bennyhuo.kotlin.deepcopy.sample.recursive

import com.bennyhuo.kotlin.deepcopy.annotations.DeepCopy

/**
 * Uncomment owner will lead to apt exception.
 * CopyLoopException: Detect infinite copy loop.
 * It will cause stack overflow to call com.bennyhuo.kotlin.deepcopy.sample.recursive.Owner.deepCopy() in the runtime.
 * */
//@DeepCopy
//data class Project(val name: String, var owner: Owner?)

/**
 * This is the correct way. Set up owner later after project initialized.
 */
@DeepCopy
data class Project(val name: String){
    lateinit var owner: Owner
}

@DeepCopy
data class Owner(val project: Project)