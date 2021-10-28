package com.bennyhuo.kotlin.deepcopy.annotations;

public @interface DeepCopyIndex {
    String[] values() default {};
}
