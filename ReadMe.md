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
implementation("com.bennyhuo.kotlin:deepcopy-reflect:1.5.31.0")
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
    id("com.google.devtools.ksp") version "1.5.31-1.0.1" // ksp
    id "org.jetbrains.kotlin.kapt" // kapt
}
...

dependencies {
    ksp("com.bennyhuo.kotlin:deepcopy-compiler-ksp:1.5.31.0")) // ksp
    kapt("com.bennyhuo.kotlin:deepcopy-compiler-kapt:1.5.31.0") // kapt
    implementation("com.bennyhuo.kotlin:deepcopy-runtime:1.5.31.0")
}
```

# Change Log

## v1.7.10.0 

Build with Kotlin 1.7.10. 

### Runtime

* Rename `DeepCopiable` to `DeepCopyable` which seems more widely used.

### Reflect

* Make `deepCopy` function available to `DeepCopyable` only.  
* [NEW] Add support for Kotlin JS.

### APT & KSP

* Support multi-module project in a unified way.

### KCP

* Generate a copy-like function `deepCopy` for data classes annotated with `@DeepCopy`.
* Generate implementation of `deepCopy` for data classes implemented `DeepCopyable`.
* Add super type `DeepCopyable` to those data classes annotated with `@DeepCopy`.
* Carefully handle manually written `deepCopy` function.
* Add support for Collections.

## v1.5.0 Reflect & Apt

Compiles on Kotlin v1.5.0. Update `kotlinx-metadata-jvm` to v0.3.0.

## v1.3.72 Apt

Compiles on Kotlin v1.3.72.

* [Bug] Fixed: rewriting DeepCopy.kt.
* [Bug] Fixed: maven dependency scope with runtime module.

## v1.3.0 Apt

* [Feature] Collections/Maps are supported. 
* [Feature] Prebuilt types are supported by annotation `DeepCopyConfig`. See [Prebuilt](apt-impl/sample/src/main/kotlin/com/bennyhuo/kotlin/deepcopy/sample/prebuilt/PrebuiltClass.kt)
* [Bug] Fixed: Data classes with their dependencies should be place into the same package.
* [Bug] Fixed: Improperly import a type variable when component type is not reified.

## v1.2.0 Apt

* [Feature] Add support for nullable types. It will allow copy loop sematically and an exception will be thrown to respond when compiling. See [Recursive.kt](apt-impl/sample/src/main/kotlin/com/bennyhuo/kotlin/deepcopy/sample/recursive/Recursive.kt).

## v1.1.0 Apt 

* [Feature] Add default values to generated `deepCopy` functions.

# License

[MIT License](https://github.com/enbandari/KotlinDeepCopy/blob/master/LICENSE)

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


