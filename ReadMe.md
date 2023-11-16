[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](LICENSE) [![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.bennyhuo.kotlin/deepcopy-reflect/badge.svg)](https://maven-badges.herokuapp.com/maven-central/com.bennyhuo.kotlin/deepcopy-reflect)


# KotlinDeepCopy

Provide an easy way to generate `DeepCopy` function for `data class`. DeepCopy only takes effect on the component members i.e. the members declared in the primary constructor.

## Reflection

Use Kotlin Reflection to provide an extension function for `DeepCopyable` so that any data class which implements `DeepCopyable` can simply call `deepCopy()` to copy itsself.

See the test code below: 

```kotlin
data class Speaker(val name: String, val age: Int): DeepCopyable

data class Talk(val name: String, val speaker: Speaker): DeepCopyable

class DeepCopyTest {
    @Test
    fun test() {
        val talk = Talk("DataClass in Action", Speaker("Benny Huo", 30))
        val newTalk = talk.deepCopy()
        assert(talk == newTalk)
        assert(talk !== newTalk)
    }
}
```

`talk` equals `newTalk` since the values of their members are equal while they do not ref to the same object.

### How to Setup

This library has been deloyed to maven center. 

```gradle
implementation("com.bennyhuo.kotlin:deepcopy-reflect:<latest-version>")
```

## Apt & Ksp

If you concern about the runtime efficiency, apt may be the solution. You can simply annotate the data class you want to deep copy with a `DeepCopy` annotation:

```kotlin
@DeepCopy
data class Speaker(val name: String, val age: Int)
@DeepCopy
data class Talk(val name: String, val speaker: Speaker)
```

Extension function `deepCopy` will be generated according to the components:

```kotlin
fun Talk.deepCopy(name: String = this.name, speaker: Speaker = this.speaker): Talk = Talk(name, speaker.deepCopy())

fun Speaker.deepCopy(
    name: String = this.name,
    age: Int = this.age,
    company: Company = this.company
): Speaker = Speaker(name, age, company.deepCopy()) 
```

Notice that `deepCopy` is called recursively if the member type is also a `data class` annotated with a `DeepCopy` annotation. Hence, if you remove the annotation for `Speaker`, generated function would be like:

```kotlin
fun Talk.deepCopy(name: String = this.name, speaker: Speaker = this.speaker): Talk = Talk(name, speaker)

//And no deepCopy for Speaker.
```

### How to Setup

The artifacts have been deployed to maven central repository. Set up your project by adding these lines:

```gradle
plugins {
    id("com.google.devtools.ksp") version "1.9.20-1.0.14" // ksp
    id "org.jetbrains.kotlin.kapt" // kapt
}
...

dependencies {
    ksp("com.bennyhuo.kotlin:deepcopy-compiler-ksp:<latest-version>")) // ksp
    kapt("com.bennyhuo.kotlin:deepcopy-compiler-kapt:<latest-version>") // kapt
    implementation("com.bennyhuo.kotlin:deepcopy-runtime:<latest-version>")
}
```

## KCP

This is a nearly perfect version I think. It works like `copy` does. You can install this IntelliJ plugin: [DeepCopy](https://plugins.jetbrains.com/plugin/19915-deepcopy-for-kotlin-data-class) and setup your project like this:

```gradle
plugins {
    kotlin("jvm") version "1.9.20"
    id("com.bennyhuo.kotlin.plugin.deepcopy") version "<latest-version>"
}

dependencies {
    implementation("com.bennyhuo.kotlin:deepcopy-runtime:<latest-version>")
}
```

And then try to call the `deepCopy` function directly!

# Change Log

See [releases](https://github.com/bennyhuo/KotlinDeepCopy/releases).

# License

[MIT License](LICENSE)

    Copyright (c) 2018 Bennyhuo
    
    Permission is hereby granted, free of charge, to any person obtaining a copy
    of this software and associated documentation files (the "Software"), to deal
    in the Software without restriction, including without limitation the rights
    to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
    copies of the Software, and to permit persons to whom the Software is
    furnished to do so, subject to the following conditions:
    
    The above copyright notice and this permission notice shall be included in all
    copies or substantial portions of the Software.
    
    THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
    IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
    FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
    AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
    LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
    OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
    SOFTWARE.


