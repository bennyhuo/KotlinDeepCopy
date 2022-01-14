// SOURCE
// FILE: Main.kt [MainKt#main]
import com.bennyhuo.kotlin.deepcopy.annotations.DeepCopy
import com.bennyhuo.kotlin.deepcopy.DeepCopiable

@DeepCopy
data class DataClass(var name: String): DeepCopiable<DataClass>

data class PlainClass(var name: String)

@DeepCopy
data class Container(val dataClass: DataClass, val plainClass: PlainClass)

data class User(var name: String, var age: Int): DeepCopiable<User>

fun main() {
    val container = Container(DataClass("x0"), PlainClass("y0"))
    val copy = container.deepCopy()

    println(container !== copy)
    println(container.dataClass !== copy.dataClass)
    // call plainClass.copy() in Container.deepCopy, this will be false
    println(container.plainClass === copy.plainClass)

    container.dataClass.name = "x1"
    container.plainClass.name = "y1"

    println(copy.dataClass.name == "x0")
    println(copy.plainClass.name == "y1")

    val user = User("bennyhuo", 10)
    val userCopy = user.deepCopy()
    println(userCopy)
    println(user === userCopy)
}

// GENERATED
// FILE: Main.kt
true
true
false
true
false
User(name=bennyhuo, age=10)
false