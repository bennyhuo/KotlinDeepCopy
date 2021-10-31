package com.bennyhuo.kotlin.deepcopy.annotations

import kotlin.reflect.KClass

annotation class DeepCopyConfig(val values: Array<KClass<*>> = emptyArray())