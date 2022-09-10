package com.bennyhuo.kotlin.deepcopy.sample

import com.bennyhuo.kotlin.deepcopy.DeepCopiable
import com.bennyhuo.kotlin.deepcopy.annotations.DeepCopy

@DeepCopy
data class District(var name: String)

@DeepCopy
data class Location(var lat: Double, var lng: Double)

@DeepCopy
data class Company(var name: String, var location: Location, var district: District)

@DeepCopy
data class Speaker(var name: String, var age: Int, var company: Company)

@DeepCopy
data class Talk(var name: String, var speaker: Speaker) {
    fun deepCopy(name: String = this.name, speaker: Speaker = this.speaker): Talk {
        return Talk(name, speaker)
    }
}

class A : DeepCopiable<A> {
    override fun deepCopy(): A {
        return A()
    }
}

@DeepCopy
data class AA(val a: A) : DeepCopiable<AA> {
    override fun deepCopy(): AA {
        return AA(a)
    }
}

data class B(val name: String)

data class DataClass(var name: String) : DeepCopiable<DataClass>

data class DataClass2(var name: String)

@DeepCopy
data class Container(val dataClasses: List<DataClass>, val dataClasses2: List<DataClass2>)


fun main(args: Array<String>) {
    val talk = Talk(
            "如何优雅地使用数据类",
            Speaker(
                    "bennyhuo 不是算命的",
                    1,
                    Company(
                            "猿辅导",
                            Location(39.9, 116.3),
                            District("北京郊区")
                    )
            )
    )

    val a = A()
    val b = a.deepCopy()

    println(a === b)

    val aa = AA(a)
    val bb = aa.deepCopy()

    println(aa === bb)
    println(aa.a === bb.a)

    val copiedTalk = talk.deepCopy()
    copiedTalk.name = "Kotlin 编译器插件：我们不期待"
    copiedTalk.speaker.company = Company(
            "猿辅导",
            Location(39.9, 116.3),
            District("华鼎世家对面")
    )
    println(talk === copiedTalk)
    println(talk.speaker === copiedTalk.speaker)

    println(talk is DeepCopiable<*>)
    println(B("Hello") as Any !is DeepCopiable<*>)

    val container = Container(
            listOf(
                    DataClass("a"),
                    DataClass("b"),
                    DataClass("c"),
                    DataClass("d"),
            ),
            listOf(
                    DataClass2("a"),
                    DataClass2("b"),
                    DataClass2("c"),
                    DataClass2("d"),
            )
    )

    val copy = container.deepCopy()
    println(container.dataClasses.zip(copy.dataClasses).all { (first, second) -> first != second })
    println(container.dataClasses2.zip(copy.dataClasses2).all { (first, second) -> first == second })
}