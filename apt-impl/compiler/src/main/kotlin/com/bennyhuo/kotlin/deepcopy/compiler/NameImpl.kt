package com.bennyhuo.kotlin.deepcopy.compiler

import javax.lang.model.element.Name

class NameImpl(private val value: String): Name {
    override fun get(index: Int): Char {
        return value[index]
    }

    override fun contentEquals(cs: CharSequence): Boolean {
        return value.contentEquals(cs)
    }

    override fun subSequence(startIndex: Int, endIndex: Int): CharSequence {
        return value.subSequence(startIndex, endIndex)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as NameImpl

        if (value != other.value) return false
        if (length != other.length) return false

        return true
    }
    override fun hashCode() = value.hashCode()
    override val length = value.length
}
