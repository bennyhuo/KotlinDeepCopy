package com.bennyhuo.kotlin.deepcopy.sample

import com.bennyhuo.kotlin.deepcopy.annotations.DeepCopy


@DeepCopy
data class GenericParameter(val map: HashMap<String, List<String>>)

@DeepCopy
data class GenericParameterT<K: Number, V>(val map: HashMap<K, V>)

@DeepCopy
data class GenericParameterOutT<out K: Number>(val map: List<K>)

@DeepCopy
data class StarProjection0(val map: List<Triple<*, String, *>>)

@DeepCopy
data class StarProjection1(val map: List<Map<*, String>>)

@DeepCopy
data class StarProjection2(val map: Map<*, *>)

@DeepCopy
data class Variances(val map: HashMap<String, out Number>)

@DeepCopy
data class Variances1(val map: HashMap<String, out List<Number>>)