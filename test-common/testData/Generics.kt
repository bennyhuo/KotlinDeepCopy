// SOURCE
import com.bennyhuo.kotlin.deepcopy.annotations.DeepCopy

@DeepCopy
data class GenericParameter(val map: HashMap<String, List<String>>)

@DeepCopy
data class GenericParameterT<K: Number, V>(val map: HashMap<K, V>)

@DeepCopy
data class GenericParameterOutT<out K: Number>(val map: List<K>)

@DeepCopy
data class StarProjection0(val list: List<Triple<*, String, *>>)

@DeepCopy
data class StarProjection1(val list: List<Map<*, String>>)

@DeepCopy
data class StarProjection2(val map: Map<*, *>)

@DeepCopy
data class StarProjection3(val list: List<*>)

@DeepCopy
data class Variances(val map: HashMap<String, out Number>)

@DeepCopy
data class Variances1(val map: HashMap<String, out List<Number>>)

// GENERATED
//-------GenericParameter$$DeepCopy.kt------
import kotlin.String
import kotlin.collections.HashMap
import kotlin.collections.List
import kotlin.jvm.JvmOverloads

@JvmOverloads
public fun GenericParameter.deepCopy(map: HashMap<String, List<String>> = this.map):
    GenericParameter = GenericParameter(map) 
//-------GenericParameterOutT$$DeepCopy.kt------
import kotlin.Number
import kotlin.collections.List
import kotlin.jvm.JvmOverloads

@JvmOverloads
public fun <out K : Number> GenericParameterOutT<K>.deepCopy(map: List<K> = this.map):
    GenericParameterOutT<K> = GenericParameterOutT<K>(map) 
//-------GenericParameterT$$DeepCopy.kt------
import kotlin.Number
import kotlin.collections.HashMap
import kotlin.jvm.JvmOverloads

@JvmOverloads
public fun <K : Number, V> GenericParameterT<K, V>.deepCopy(map: HashMap<K, V> = this.map):
    GenericParameterT<K, V> = GenericParameterT<K, V>(map) 
//-------StarProjection0$$DeepCopy.kt------
import kotlin.String
import kotlin.Triple
import kotlin.collections.List
import kotlin.jvm.JvmOverloads

@JvmOverloads
public fun StarProjection0.deepCopy(list: List<Triple<*, String, *>> = this.list): StarProjection0 =
    StarProjection0(list) 
//-------StarProjection1$$DeepCopy.kt------
import kotlin.String
import kotlin.collections.List
import kotlin.collections.Map
import kotlin.jvm.JvmOverloads

@JvmOverloads
public fun StarProjection1.deepCopy(list: List<Map<*, String>> = this.list): StarProjection1 =
    StarProjection1(list) 
//-------StarProjection2$$DeepCopy.kt------
import kotlin.collections.Map
import kotlin.jvm.JvmOverloads

@JvmOverloads
public fun StarProjection2.deepCopy(map: Map<*, *> = this.map): StarProjection2 =
    StarProjection2(map) 
//-------StarProjection3$$DeepCopy.kt------
import kotlin.collections.List
import kotlin.jvm.JvmOverloads

@JvmOverloads
public fun StarProjection3.deepCopy(list: List<*> = this.list): StarProjection3 =
    StarProjection3(list) 
//-------Variances$$DeepCopy.kt------
import kotlin.Number
import kotlin.String
import kotlin.collections.HashMap
import kotlin.jvm.JvmOverloads

@JvmOverloads
public fun Variances.deepCopy(map: HashMap<String, out Number> = this.map): Variances =
    Variances(map) 
//-------Variances1$$DeepCopy.kt------
import kotlin.Number
import kotlin.String
import kotlin.collections.HashMap
import kotlin.collections.List
import kotlin.jvm.JvmOverloads

@JvmOverloads
public fun Variances1.deepCopy(map: HashMap<String, out List<Number>> = this.map): Variances1 =
    Variances1(map) 

