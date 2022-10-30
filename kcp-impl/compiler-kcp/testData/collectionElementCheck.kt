// SOURCE
// FILE: Main.kt [MainKt#main]
import com.bennyhuo.kotlin.deepcopy.annotations.DeepCopy
import com.bennyhuo.kotlin.deepcopy.DeepCopyable

data class DataClass(var name: String) : DeepCopyable<DataClass>
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

    val copy = container.deepCopy()
    println(container.dataClasses.zip(copy.dataClasses).all { (first, second) -> first === second })
    println(container.dataClasses2.zip(copy.dataClasses2).all { (first, second) -> first === second })
}

// EXPECT
// FILE: Main.kt.stdout
false
true