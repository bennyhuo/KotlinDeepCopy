// SOURCE
// FILE: Main.kt [MainKt#main]
import com.bennyhuo.kotlin.deepcopy.annotations.DeepCopy

@DeepCopy
data class DataClass(val name: String) {
    override fun deepCopy() : DataClass {
        return DataClass(name)
    }
}

@DeepCopy
data class Container(val dataClass: DataClass, val id: Int)

class PlainClass(val name: String)

fun main() {
    val container = Container(DataClass("x"), 0)
    val copy = container.deepCopy()
    println(copy)
}

// GENERATED
// FILE: Main.kt
Container(dataClass=DataClass(name=x), id=0)