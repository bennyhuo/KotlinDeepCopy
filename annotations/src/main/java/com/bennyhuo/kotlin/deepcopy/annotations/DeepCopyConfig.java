package com.bennyhuo.kotlin.deepcopy.annotations;

public @interface DeepCopyConfig {
    Class[] values() default {};
}
