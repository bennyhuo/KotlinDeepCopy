// SOURCE
package com.bennyhuo.kotlin.deepcopy.sample

import com.bennyhuo.kotlin.deepcopy.annotations.DeepCopy

@DeepCopy
data class District(val name: String)

@DeepCopy
data class Location(val lat: Double, val lng: Double)

@DeepCopy
data class Company(val name: String, val location: Location, val district: District)

@DeepCopy
data class Speaker(val name: String, val age: Int, val company: Company)

@DeepCopy
data class Talk(val name: String, val speaker: Speaker)

// GENERATED
//-------Company$$DeepCopy.kt------
package com.bennyhuo.kotlin.deepcopy.sample

import com.bennyhuo.kotlin.deepcopy.sample.deepCopy
import kotlin.String

public fun Company.deepCopy(
    name: String = this.name,
    location: Location = this.location,
    district: District = this.district
): Company = Company(name, location.deepCopy(), district.deepCopy())
//-------District$$DeepCopy.kt------
package com.bennyhuo.kotlin.deepcopy.sample

import kotlin.String

public fun District.deepCopy(name: String = this.name): District = District(name)
//-------Location$$DeepCopy.kt------
package com.bennyhuo.kotlin.deepcopy.sample

import kotlin.Double

public fun Location.deepCopy(lat: Double = this.lat, lng: Double = this.lng): Location =
    Location(lat, lng)
//-------Speaker$$DeepCopy.kt------
package com.bennyhuo.kotlin.deepcopy.sample

import com.bennyhuo.kotlin.deepcopy.sample.deepCopy
import kotlin.Int
import kotlin.String

public fun Speaker.deepCopy(
    name: String = this.name,
    age: Int = this.age,
    company: Company = this.company
): Speaker = Speaker(name, age, company.deepCopy())
//-------Talk$$DeepCopy.kt------
package com.bennyhuo.kotlin.deepcopy.sample

import com.bennyhuo.kotlin.deepcopy.sample.deepCopy
import kotlin.String

public fun Talk.deepCopy(name: String = this.name, speaker: Speaker = this.speaker): Talk =
    Talk(name, speaker.deepCopy())


