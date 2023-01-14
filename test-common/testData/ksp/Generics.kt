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

// EXPECT
// FILE: GenericParameter$$DeepCopy.kt
import kotlin.String
import kotlin.collections.HashMap
import kotlin.collections.List
import kotlin.jvm.JvmOverloads

@JvmOverloads
public fun GenericParameter.deepCopy(map: HashMap<String, List<String>> = this.map):
    GenericParameter = GenericParameter(map) 
// FILE: GenericParameterOutT$$DeepCopy.kt
import com.bennyhuo.kotlin.deepcopy.runtime.deepCopy
import kotlin.Number
import kotlin.collections.List
import kotlin.jvm.JvmOverloads

@JvmOverloads
public fun <K : Number> GenericParameterOutT<K>.deepCopy(map: List<K> = this.map):
    GenericParameterOutT<K> = GenericParameterOutT<K>(map.deepCopy())
// FILE: GenericParameterT$$DeepCopy.kt
import kotlin.Number
import kotlin.collections.HashMap
import kotlin.jvm.JvmOverloads

@JvmOverloads
public fun <K : Number, V> GenericParameterT<K, V>.deepCopy(map: HashMap<K, V> = this.map):
    GenericParameterT<K, V> = GenericParameterT<K, V>(map)
// FILE: StarProjection0$$DeepCopy.kt
import com.bennyhuo.kotlin.deepcopy.runtime.deepCopy
import kotlin.String
import kotlin.Triple
import kotlin.collections.List
import kotlin.jvm.JvmOverloads

@JvmOverloads
public fun StarProjection0.deepCopy(list: List<Triple<*, String, *>> = this.list): StarProjection0 =
    StarProjection0(list.deepCopy())
// FILE: StarProjection1$$DeepCopy.kt
import com.bennyhuo.kotlin.deepcopy.runtime.deepCopy
import kotlin.String
import kotlin.collections.List
import kotlin.collections.Map
import kotlin.jvm.JvmOverloads

@JvmOverloads
public fun StarProjection1.deepCopy(list: List<Map<*, String>> = this.list): StarProjection1 =
    StarProjection1(list.deepCopy())
// FILE: StarProjection2$$DeepCopy.kt
import com.bennyhuo.kotlin.deepcopy.runtime.deepCopy
import kotlin.collections.Map
import kotlin.jvm.JvmOverloads

@JvmOverloads
public fun StarProjection2.deepCopy(map: Map<*, *> = this.map): StarProjection2 =
    StarProjection2(map.deepCopy({ it }, { it }))
// FILE: StarProjection3$$DeepCopy.kt
import com.bennyhuo.kotlin.deepcopy.runtime.deepCopy
import kotlin.collections.List
import kotlin.jvm.JvmOverloads

@JvmOverloads
public fun StarProjection3.deepCopy(list: List<*> = this.list): StarProjection3 =
    StarProjection3(list.deepCopy())
// FILE: Variances$$DeepCopy.kt
import kotlin.Number
import kotlin.String
import kotlin.collections.HashMap
import kotlin.jvm.JvmOverloads

@JvmOverloads
public fun Variances.deepCopy(map: HashMap<String, out Number> = this.map): Variances =
    Variances(map)
// FILE: Variances1$$DeepCopy.kt
import kotlin.Number
import kotlin.String
import kotlin.collections.HashMap
import kotlin.collections.List
import kotlin.jvm.JvmOverloads

@JvmOverloads
public fun Variances1.deepCopy(map: HashMap<String, out List<Number>> = this.map): Variances1 =
    Variances1(map)

