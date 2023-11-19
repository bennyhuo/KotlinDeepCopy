package com.bennyhuo.kotlin.deepcopy.ide

import org.jetbrains.kotlin.idea.core.ShortenReferences
import org.jetbrains.kotlin.psi.KtClass
import org.jetbrains.kotlin.psi.KtElement

/**
 * Created by benny at 2022/1/15 3:55 PM.
 */
fun <T : KtElement> T.shortenReferences() = ShortenReferences.DEFAULT.process(this)

fun KtClass.isDataClassLike() = hasPrimaryConstructor() && primaryConstructorParameters.let {
    it.isNotEmpty() && it.all { it.hasValOrVar() }
}

inline fun <reified T> Any?.safeAs(): T? = this as? T
