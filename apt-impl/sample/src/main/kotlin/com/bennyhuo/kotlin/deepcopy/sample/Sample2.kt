package com.bennyhuo.kotlin.deepcopy.sample

import com.bennyhuo.kotlin.deepcopy.annotations.DeepCopy

@DeepCopy
data class GitUser(val name: String)

@DeepCopy
data class Project(val name: String)

@DeepCopy
data class Owner(val gitUser: GitUser, val project: Project)