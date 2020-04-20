# KotlinDeepCopy

Provide an easy way to generate `DeepCopy` function for `data class`. DeepCopy only takes effect on the component members i.e. the members declared in the primary constructor.

## Reflection

Use Kotlin Reflection to provide an extension function for `Any` so that any data class can simple call `deepCopy()` to copy itsself.

See the test code below: 

```kotlin
data class Speaker(val name: String, val age: Int)

data class Talk(val name: String, val speaker: Speaker)

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

This library has been deloyed to jcenter. 

```gradle
compile 'com.bennyhuo.kotlin:deepcopy-reflect:1.1.0'
```

## Apt

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

This library has been deloyed to jcenter, but the compiler depends on the experimental artifact `kotlinx-metadata-jvm` so you should also add a maven repo `https://kotlin.bintray.com/kotlinx/` to compile.

```gradle

repositories {
    jcenter()
    maven { url "https://kotlin.bintray.com/kotlinx/" }
}

...
apply plugin: "kotlin-kapt"
...

dependencies {
    kapt 'com.bennyhuo.kotlin:deepcopy-compiler:1.3.0'
    implementation 'com.bennyhuo.kotlin:deepcopy-runtime:1.3.0'
}
```

# Change Log

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


