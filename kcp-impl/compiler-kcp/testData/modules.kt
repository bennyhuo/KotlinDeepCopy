// SOURCE
// MODULE: lib-deepcopy
// FILE: DeepCopy.java
package com.bennyhuo.kotlin.deepcopy.annotations;

public @interface DeepCopy {
}

// MODULE: lib-user / lib-deepcopy
// FILE: User.kt
import com.bennyhuo.kotlin.deepcopy.annotations.DeepCopy

@DeepCopy
data class User(var name: String)

// MODULE: lib-project / lib-user
// FILE: User.kt
import com.bennyhuo.kotlin.deepcopy.annotations.DeepCopy

@DeepCopy
data class Project(var name: String, var owner: User)

// MODULE: lib-country / lib-deepcopy
// FILE: Country.kt
import com.bennyhuo.kotlin.deepcopy.annotations.DeepCopy

@DeepCopy
data class Country(var name: String)

// MODULE: main / lib-project, lib-country
// FILE: Main.kt [MainKt#main]
import com.bennyhuo.kotlin.deepcopy.annotations.DeepCopy

@DeepCopy
data class Company(
    val name: String,
    val country: Country,
    val projects: List<Project>
)

fun main() {
    val company = Company(
        "JetBrains",
        Country("Czech"),
        listOf(
            Project("IntelliJ", User("JetBrains")),
            Project("Kotlin", User("JetBrains"))
        )
    )

    val company2 = company.deepCopy()
    println(company)
    println(company2)

    company2.country.name = "Czech Republic"

    println(company)
    println(company2)

    println(company.projects == company2.projects)
}
// GENERATED
// MODULE: main
// FILE: Main.kt
Company(name=JetBrains, country=Country(name=Czech), projects=[Project(name=IntelliJ, owner=User(name=JetBrains)), Project(name=Kotlin, owner=User(name=JetBrains))])
Company(name=JetBrains, country=Country(name=Czech), projects=[Project(name=IntelliJ, owner=User(name=JetBrains)), Project(name=Kotlin, owner=User(name=JetBrains))])
Company(name=JetBrains, country=Country(name=Czech), projects=[Project(name=IntelliJ, owner=User(name=JetBrains)), Project(name=Kotlin, owner=User(name=JetBrains))])
Company(name=JetBrains, country=Country(name=Czech Republic), projects=[Project(name=IntelliJ, owner=User(name=JetBrains)), Project(name=Kotlin, owner=User(name=JetBrains))])
false