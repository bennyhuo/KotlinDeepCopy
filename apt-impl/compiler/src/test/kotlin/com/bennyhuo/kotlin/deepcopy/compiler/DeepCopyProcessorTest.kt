package com.bennyhuo.kotlin.deepcopy.compiler

import kotlinx.metadata.jvm.KotlinClassHeader
import kotlinx.metadata.jvm.KotlinClassMetadata
import org.junit.Test
import kotlin.reflect.KClass

data class Talk(val id: Int, val name: String, val speaker: Speaker){
    data class Speaker(val id: Int, val name: String, val age: Int)
}

class DeepCopyProcessorTest{

    @Test
    fun testMetaData(){
        Talk::class.testMetadata()
        DeepCopyProcessorTest::class.testMetadata()
    }

    fun KClass<*>.testMetadata() {
        val metaData = this.java.getAnnotation(Metadata::class.java)
        val metaDataParsed = KotlinClassMetadata.read(
            KotlinClassHeader(metaData.kind,
                metaData.metadataVersion,
                metaData.bytecodeVersion, metaData.data1, metaData.data2, metaData.extraString, metaData.packageName, metaData.extraInt)
        )
        when(metaDataParsed){
            is KotlinClassMetadata.Class -> {
                println(KClassMetadata(metaDataParsed))
            }
        }
    }

}