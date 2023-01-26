// SOURCE
import com.bennyhuo.kotlin.deepcopy.annotations.DeepCopy
import com.bennyhuo.kotlin.deepcopy.DeepCopyable

@DeepCopy
data class GenericParameter<A: Number, B: DeepCopyable<B>>(val a: A, val b: B, val c: List<A>, val d: List<B>)

// EXPECT
// FILE: GenericParameter$$DeepCopy.kt
import com.bennyhuo.kotlin.deepcopy.DeepCopyable
import com.bennyhuo.kotlin.deepcopy.runtime.deepCopy
import kotlin.Number
import kotlin.collections.List
import kotlin.jvm.JvmOverloads

@JvmOverloads
public fun <A : Number, B : DeepCopyable<B>> GenericParameter<A, B>.deepCopy(
  a: A = this.a,
  b: B = this.b,
  c: List<A> = this.c,
  d: List<B> = this.d,
): GenericParameter<A, B> = GenericParameter<A, B>(a, b.deepCopy(), c.deepCopy(), d.deepCopy {
    it.deepCopy() })

