package com.bennyhuo.kotlin.deepcopy.sample

import com.bennyhuo.kotlin.deepcopy.annotations.DeepCopy

class UserInfo(var name: String, var age: Int, var bio: String)
data class User(var id: Long, var info: UserInfo)

@DeepCopy
data class Project(var name: String, var owner: User)