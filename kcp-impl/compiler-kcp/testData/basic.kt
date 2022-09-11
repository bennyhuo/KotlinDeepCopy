// SOURCE
// FILE: Main.kt [MainKt#main]
import com.bennyhuo.kotlin.deepcopy.annotations.DeepCopy

@DeepCopy
data class DataClass(var name: String)
data class PlainClass(var name: String)

@DeepCopy
data class Container(val dataClass: DataClass, val plainClass: PlainClass)

@DeepCopy
data class User(var name: String, var id: Long) {
    fun deepCopy(name: String = this.name, id: Long = this.id): User {
        return User("bennyhuo", id)
    }
}


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

    val user = User("benny", 1)
    println(user.deepCopy(id = 0).name == "bennyhuo")
}

// GENERATED
// FILE: Main.kt
true
true
false
true
false
true