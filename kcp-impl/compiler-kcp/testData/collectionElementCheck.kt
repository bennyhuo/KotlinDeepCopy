// SOURCE
// FILE: Main.kt
import com.bennyhuo.kotlin.deepcopy.annotations.DeepCopy
import com.bennyhuo.kotlin.deepcopy.DeepCopiable

data class DataClass(var name: String): DeepCopiable<DataClass>
data class DataClass2(var name: String)

@DeepCopy
data class Container(val dataClasses: List<DataClass>, val dataClasses2: List<DataClass2>)

fun main() {
    val container = Container(
        listOf(
            DataClass("a"),
            DataClass("b"),
            DataClass("c"),
            DataClass("d"),
        ),
        listOf(
            DataClass2("a"),
            DataClass2("b"),
            DataClass2("c"),
            DataClass2("d"),
        )
    )

    println(container)
}

// GENERATED
// FILE: Main.kt
