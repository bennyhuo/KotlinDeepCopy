// SOURCE
import com.bennyhuo.kotlin.deepcopy.annotations.DeepCopy

@DeepCopy
data class Team(val name: String, val workers: List<Worker>)

@DeepCopy
data class Worker(val name: String)

@DeepCopy
data class Team2(val name: String, val workers: List<Worker2>, val pair: Pair<String, Worker2>, val map: Map<String, Worker2>){
    @DeepCopy
    data class Worker2(val name: String)
}

// EXPECT
// FILE: Team$$DeepCopy.kt
import com.bennyhuo.kotlin.deepcopy.runtime.deepCopy
import deepCopy
import kotlin.String
import kotlin.collections.List
import kotlin.jvm.JvmOverloads

@JvmOverloads
public fun Team.deepCopy(name: String = this.name, workers: List<Worker> = this.workers): Team =
    Team(name, workers.deepCopy { it.deepCopy() })
// FILE: Worker$$DeepCopy.kt
import kotlin.String
import kotlin.jvm.JvmOverloads

@JvmOverloads
public fun Worker.deepCopy(name: String = this.name): Worker = Worker(name)
// FILE: Team2$$DeepCopy.kt
import com.bennyhuo.kotlin.deepcopy.runtime.deepCopy
import deepCopy
import kotlin.Pair
import kotlin.String
import kotlin.collections.List
import kotlin.collections.Map
import kotlin.jvm.JvmOverloads

@JvmOverloads
public fun Team2.deepCopy(
  name: String = this.name,
  workers: List<Team2.Worker2> = this.workers,
  pair: Pair<String, Team2.Worker2> = this.pair,
  map: Map<String, Team2.Worker2> = this.map,
): Team2 = Team2(name, workers.deepCopy { it.deepCopy() }, pair, map.deepCopy({ it }, {
    it.deepCopy() }))
// FILE: Worker2$$DeepCopy.kt
import kotlin.String
import kotlin.jvm.JvmOverloads

@JvmOverloads
public fun Team2.Worker2.deepCopy(name: String = this.name): Team2.Worker2 = Team2.Worker2(name)

