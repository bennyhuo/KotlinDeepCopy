// SOURCE
import com.bennyhuo.kotlin.deepcopy.annotations.DeepCopy

typealias X<K, V> = HashMap<K,V>

@DeepCopy
data class GenericParameter(val map: X<String, List<String>>)

@DeepCopy
data class GenericParameterT<K: Number, V>(val map: X<K, V>)

// GENERATED
//-------GenericParameter$$DeepCopy.kt------
import kotlin.String
import kotlin.collections.List
import kotlin.jvm.JvmOverloads

@JvmOverloads
public fun GenericParameter.deepCopy(map: X<String, List<String>> = this.map): GenericParameter =
    GenericParameter(map) 
//-------GenericParameterT$$DeepCopy.kt------
import kotlin.Number
import kotlin.jvm.JvmOverloads

@JvmOverloads
public fun <K : Number, V> GenericParameterT<K, V>.deepCopy(map: X<K, V> = this.map):
    GenericParameterT<K, V> = GenericParameterT<K, V>(map) 

