// SOURCE
// FILE: a.kt
import com.bennyhuo.kotlin.deepcopy.annotations.DeepCopyConfig
import com.bennyhuo.kotlin.deepcopy.annotations.DeepCopy

@DeepCopyConfig(values = [Pair::class, Triple::class])
class Config

@DeepCopyConfig(values = [Pair::class])
class ConfigSingle

// FILE: b.kt
package test.b

import com.bennyhuo.kotlin.deepcopy.annotations.DeepCopyConfig
import com.bennyhuo.kotlin.deepcopy.annotations.DeepCopy

@DeepCopy
data class B(
    val pair: Pair<String, Any>,
    var triple: Triple<String, Int, Any>)

// FILE: c.kt
package test.c

import com.bennyhuo.kotlin.deepcopy.annotations.DeepCopyConfig
import com.bennyhuo.kotlin.deepcopy.annotations.DeepCopy
import test.b.B

@DeepCopy
data class C(
    val pair: Pair<String, Any>,
    var triple: Triple<String, Int, Any>, 
    val b: B)


// EXPECT
// FILE: Pair$$DeepCopy.kt
package com.bennyhuo.kotlin.deepcopy.builtin

import kotlin.Pair
import kotlin.jvm.JvmOverloads

@JvmOverloads
public fun <A, B> Pair<A, B>.deepCopy(first: A = this.first, second: B = this.second): Pair<A, B> =
    Pair<A, B>(first, second) 
// FILE: Triple$$DeepCopy.kt
package com.bennyhuo.kotlin.deepcopy.builtin

import kotlin.Triple
import kotlin.jvm.JvmOverloads

@JvmOverloads
public fun <A, B, C> Triple<A, B, C>.deepCopy(
  first: A = this.first,
  second: B = this.second,
  third: C = this.third,
): Triple<A, B, C> = Triple<A, B, C>(first, second, third) 
// FILE: DeepCopyIndex_1d3454b57a92b7b99ce53008332af4b8.kt
package com.bennyhuo.kotlin.processor.module

@LibraryIndex(value = ["ConfigSingle|0", "Config|0"])
public class DeepCopyIndex_1d3454b57a92b7b99ce53008332af4b8
// FILE: B$$DeepCopy.kt
package test.b

import com.bennyhuo.kotlin.deepcopy.builtin.deepCopy
import kotlin.Any
import kotlin.Int
import kotlin.Pair
import kotlin.String
import kotlin.Triple
import kotlin.jvm.JvmOverloads

@JvmOverloads
public fun B.deepCopy(pair: Pair<String, Any> = this.pair, triple: Triple<String, Int, Any> =
    this.triple): B = B(pair.deepCopy(), triple.deepCopy()) 
// FILE: C$$DeepCopy.kt
package test.c

import com.bennyhuo.kotlin.deepcopy.builtin.deepCopy
import kotlin.Any
import kotlin.Int
import kotlin.Pair
import kotlin.String
import kotlin.Triple
import kotlin.jvm.JvmOverloads
import test.b.B
import test.b.deepCopy

@JvmOverloads
public fun C.deepCopy(
  pair: Pair<String, Any> = this.pair,
  triple: Triple<String, Int, Any> = this.triple,
  b: B = this.b,
): C = C(pair.deepCopy(), triple.deepCopy(), b.deepCopy()) 

