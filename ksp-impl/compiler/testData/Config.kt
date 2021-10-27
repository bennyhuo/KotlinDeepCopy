// SOURCE
import com.bennyhuo.kotlin.deepcopy.annotations.DeepCopyConfig

@DeepCopyConfig(values = [Pair::class, Triple::class])
class Config

@DeepCopyConfig(values = Pair::class)
class ConfigSingle

// GENERATE
//-------Pair$$DeepCopy.kt------
package kotlin

import kotlin.jvm.JvmOverloads

@JvmOverloads
public fun <out A, out B> Pair<A, B>.deepCopy(first: A = this.first, second: B = this.second):
    Pair<A, B> = Pair<A, B>(first, second) 
//-------Triple$$DeepCopy.kt------
package kotlin

import kotlin.jvm.JvmOverloads

@JvmOverloads
public fun <out A, out B, out C> Triple<A, B, C>.deepCopy(
  first: A = this.first,
  second: B = this.second,
  third: C = this.third
): Triple<A, B, C> = Triple<A, B, C>(first, second, third) 
