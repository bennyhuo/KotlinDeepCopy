package com.bennyhuo.kotlin.deepcopy.compiler.kcp.utils

import com.bennyhuo.kotlin.deepcopy.compiler.kcp.DEEP_COPY_INTERFACE_NAME
import org.jetbrains.kotlin.js.descriptorUtils.getJetTypeFqName
import org.jetbrains.kotlin.types.KotlinType
import org.jetbrains.kotlin.types.typeUtil.supertypes

/**
 * Created by benny.
 */
fun KotlinType.isDeepCopyable(): Boolean {
    return supertypes().any {
        it.getJetTypeFqName(false) == DEEP_COPY_INTERFACE_NAME
    }
}