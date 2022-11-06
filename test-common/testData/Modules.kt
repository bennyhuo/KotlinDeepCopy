// SOURCE
// MODULE: lib-a
// FILE: Point.kt
data class Point(var x: Int, var y: Int)
// MODULE: lib-b  / lib-a
// FILE: Config.kt
import com.bennyhuo.kotlin.deepcopy.annotations.DeepCopyConfig

@DeepCopyConfig(values = [Point::class])
class Config
// MODULE: main / lib-a, lib-b
// FILE: Location.kt
import com.bennyhuo.kotlin.deepcopy.annotations.DeepCopy
@DeepCopy
data class Location(val name: String, val pointE06: Point)
// EXPECT
// MODULE: main
// FILE: Location$$DeepCopy.kt
import deepCopy
import kotlin.String
import kotlin.jvm.JvmOverloads

@JvmOverloads
public fun Location.deepCopy(name: String = this.name, pointE06: Point = this.pointE06): Location =
    Location(name, pointE06.deepCopy())