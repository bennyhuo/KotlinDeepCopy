package com.bennyhuo.kotlin.deepcopy.sample.`typealias`

import com.bennyhuo.kotlin.deepcopy.annotations.DeepCopy

typealias X<K, V> = HashMap<K,V>

@DeepCopy
data class GenericParameter(val map: X<String, List<String>>)

@DeepCopy
data class GenericParameterT<K: Number, V>(val map: X<K, V>)
