// SOURCE
import com.bennyhuo.kotlin.deepcopy.annotations.DeepCopy
import com.bennyhuo.kotlin.deepcopy.builtin.deepCopy

@DeepCopy
data class User(val name: String)

@DeepCopy
data class Nullables(val user: User?, val list: List<User>?)

// GENERATED
//-------Nullables$$DeepCopy.kt------
import kotlin.collections.List
import kotlin.jvm.JvmOverloads

@JvmOverloads
public fun Nullables.deepCopy(user: User? = this.user, list: List<User>? = this.list): Nullables =
    Nullables(user?.deepCopy(), list) 
//-------User$$DeepCopy.kt------
import kotlin.String
import kotlin.jvm.JvmOverloads

@JvmOverloads
public fun User.deepCopy(name: String = this.name): User = User(name) 

