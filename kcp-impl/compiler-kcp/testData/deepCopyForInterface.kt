// SOURCE
// FILE: Main.kt [MainKt#main]
import com.bennyhuo.kotlin.deepcopy.annotations.DeepCopy
import com.bennyhuo.kotlin.deepcopy.DeepCopiable

@DeepCopy
data class DataClass(var name: String): DeepCopiable<DataClass>

data class PlainClass(var name: String)

@DeepCopy
data class Container(val dataClass: DataClass, val plainClass: PlainClass)

fun main() {
    val container = Container(DataClass("x0"), PlainClass("y0"))
    val copy = container.deepCopy()

    println(container !== copy)
    println(container.dataClass !== copy.dataClass)
    println(container.plainClass === copy.plainClass)

    container.dataClass.name = "x1"
    container.plainClass.name = "y1"

    println(copy.dataClass.name == "x0")
    println(copy.plainClass.name == "y1")
}

// GENERATED
// FILE: Main.kt
true
true
true
true
true