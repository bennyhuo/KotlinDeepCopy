// SOURCE
// FILE: Main.kt [MainKt#main]
import com.bennyhuo.kotlin.deepcopy.annotations.DeepCopy
import com.bennyhuo.kotlin.deepcopy.DeepCopyable

@DeepCopy
data class DataClass(var value: String)

fun main() {
    val dataClass = DataClass("hello")
    println(dataClass.deepCopy("world").value == "world")
    println(dataClass is DeepCopyable<*>)
}

// EXPECT
// FILE: MainKt.main.stdout
true
true
// FILE: Main.kt.ir
@DeepCopy
data class DataClass(var value: String) : DeepCopyable<DataClass> {
    fun component1(): String {
        return <this>.value
    }
    fun copy(value: String = <this>.value): DataClass {
        return DataClass(value)
    }
    override fun toString(): String {
        return "DataClass(value=${<this>.value})"
    }
    override fun hashCode(): Int {
        return <this>.value.hashCode()
    }
    override fun equals(other: Any?): Boolean {
        if (<this> === other) {
            return true
        }
        if (other) {
            return false
        }
        val tmp0_other_with_cast = other
        if (<this>.value != tmp0_other_with_cast.value) {
            return false
        }
        return true
    }
    fun deepCopy(value: String = <this>.value): DataClass {
        return DataClass(value)
    }
    override fun deepCopy(): DataClass {
        return DataClass(<this>.value)
    }
}
fun main() {
    val dataClass = DataClass("hello")
    println(dataClass.deepCopy("world").value == "world")
    println(dataClass)
}