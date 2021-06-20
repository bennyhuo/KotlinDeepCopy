package com.bennyhuo.kotlin.deepcopy.sample

import com.bennyhuo.kotlin.deepcopy.annotations.DeepCopy

/**
 * Created by benny at 2021/6/21 7:03.
 */
@DeepCopy
data class District(val name: String)

@DeepCopy
data class Location(val lat: Double, val lng: Double)

@DeepCopy
data class Company(val name: String, val location: Location, val district: District)

@DeepCopy
data class Speaker(val name: String, val age: Int, val company: Company)

@DeepCopy
data class Talk(val name: String, val speaker: Speaker)