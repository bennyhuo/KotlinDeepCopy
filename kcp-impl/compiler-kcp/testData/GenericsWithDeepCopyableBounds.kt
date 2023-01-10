// SOURCE
// FILE: Main.kt
import com.bennyhuo.kotlin.deepcopy.annotations.DeepCopy
import com.bennyhuo.kotlin.deepcopy.DeepCopyable

@DeepCopy
data class GenericParameter<A: Number, B: DeepCopyable<B>>(val a: A, val b: B, val c: List<A>, val d: List<B>)

// EXPECT
// FILE: Main.kt.ir
@DeepCopy
data class GenericParameter<A: Number, B: DeepCopyable<B>> (val a: A, val b: B, val c: List<A>, val d: List<B>) : DeepCopyable<GenericParameter<A, B>> {
    fun component1(): A {
        return <this>.a
    }
    fun component2(): B {
        return <this>.b
    }
    fun component3(): List<A> {
        return <this>.c
    }
    fun component4(): List<B> {
        return <this>.d
    }
    fun copy(a: A = <this>.a, b: B = <this>.b, c: List<A> = <this>.c, d: List<B> = <this>.d): GenericParameter<A, B> {
        return GenericParameter(a, b, c, d)
    }
    override fun toString(): String {
        return "GenericParameter(a=${<this>.a}, b=${<this>.b}, c=${<this>.c}, d=${<this>.d})"
    }
    override fun hashCode(): Int {
        var result = <this>.a.hashCode()
        result = result * 31 + <this>.b.hashCode()
        result = result * 31 + <this>.c.hashCode()
        result = result * 31 + <this>.d.hashCode()
        return result
    }
    override fun equals(other: Any?): Boolean {
        if (<this> === other) {
            return true
        }
        if (other) {
            return false
        }
        val tmp0_other_with_cast = other
        if (<this>.a != tmp0_other_with_cast.a) {
            return false
        }
        if (<this>.b != tmp0_other_with_cast.b) {
            return false
        }
        if (<this>.c != tmp0_other_with_cast.c) {
            return false
        }
        if (<this>.d != tmp0_other_with_cast.d) {
            return false
        }
        return true
    }
    fun deepCopy(a: A = <this>.a, b: B = <this>.b, c: List<A> = <this>.c, d: List<B> = <this>.d): GenericParameter<A, B> {
        return GenericParameter(a, b.deepCopy(), c.deepCopy(), d.deepCopy())
    }
    override fun deepCopy(): GenericParameter<A, B> {
        return GenericParameter(<this>.a, <this>.b.deepCopy(), <this>.c.deepCopy(), <this>.d.deepCopy())
    }
}