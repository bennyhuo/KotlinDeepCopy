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

// GENERATED