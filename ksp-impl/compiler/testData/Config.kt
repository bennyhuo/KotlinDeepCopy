// SOURCE
import com.bennyhuo.kotlin.deepcopy.annotations.DeepCopyConfig

@DeepCopyConfig(values = [Pair::class, Triple::class])
class Config

@DeepCopyConfig(values = Pair::class)
class ConfigSingle

// GENERATED
//-------Pair$$DeepCopy.kt------
package com.bennyhuo.kotlin.deepcopy.builtin

import kotlin.Pair
import kotlin.jvm.JvmOverloads

@JvmOverloads
public fun <out A, out B> Pair<A, B>.deepCopy(first: A = this.first, second: B = this.second):
    Pair<A, B> = Pair<A, B>(first, second) 
//-------Triple$$DeepCopy.kt------
package com.bennyhuo.kotlin.deepcopy.builtin

import kotlin.Triple
import kotlin.jvm.JvmOverloads

@JvmOverloads
public fun <out A, out B, out C> Triple<A, B, C>.deepCopy(
  first: A = this.first,
  second: B = this.second,
  third: C = this.third
): Triple<A, B, C> = Triple<A, B, C>(first, second, third) 

