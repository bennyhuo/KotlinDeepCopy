// SOURCE
import com.bennyhuo.kotlin.deepcopy.annotations.DeepCopy

@DeepCopy
data class Talk(val name: String, val speaker: Speaker) {
    @DeepCopy
    data class District(val name: String)

    @DeepCopy
    data class Location(val lat: Double, val lng: Double)

    @DeepCopy
    data class Company(val name: String, val location: Location, val district: District)

    @DeepCopy
    data class Speaker(val name: String, val age: Int, val company: Company)
}

// EXPECT
// FILE: Company$$DeepCopy.kt
import deepCopy
import kotlin.String
import kotlin.jvm.JvmOverloads

@JvmOverloads
public fun Talk.Company.deepCopy(
  name: String = this.name,
  location: Talk.Location = this.location,
  district: Talk.District = this.district,
): Talk.Company = Talk.Company(name, location.deepCopy(), district.deepCopy())
// FILE: District$$DeepCopy.kt
import kotlin.String
import kotlin.jvm.JvmOverloads

@JvmOverloads
public fun Talk.District.deepCopy(name: String = this.name): Talk.District = Talk.District(name)
// FILE: Location$$DeepCopy.kt
import kotlin.Double
import kotlin.jvm.JvmOverloads

@JvmOverloads
public fun Talk.Location.deepCopy(lat: Double = this.lat, lng: Double = this.lng): Talk.Location =
    Talk.Location(lat, lng)
// FILE: Speaker$$DeepCopy.kt
import deepCopy
import kotlin.Int
import kotlin.String
import kotlin.jvm.JvmOverloads

@JvmOverloads
public fun Talk.Speaker.deepCopy(
  name: String = this.name,
  age: Int = this.age,
  company: Talk.Company = this.company,
): Talk.Speaker = Talk.Speaker(name, age, company.deepCopy())
// FILE: Talk$$DeepCopy.kt
import deepCopy
import kotlin.String
import kotlin.jvm.JvmOverloads

@JvmOverloads
public fun Talk.deepCopy(name: String = this.name, speaker: Talk.Speaker = this.speaker): Talk =
    Talk(name, speaker.deepCopy())
