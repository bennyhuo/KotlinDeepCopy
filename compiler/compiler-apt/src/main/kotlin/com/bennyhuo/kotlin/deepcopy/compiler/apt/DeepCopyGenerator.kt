package com.bennyhuo.kotlin.deepcopy.compiler.apt

import com.bennyhuo.aptutils.AptContext
import com.bennyhuo.aptutils.types.packageName
import com.bennyhuo.aptutils.types.simpleName
import com.squareup.kotlinpoet.*

class DeepCopyGenerator(val kTypeElement: KTypeElement){

    companion object {
        const val POSIX = "$\$DeepCopy"
    }

    fun generate(){
        val fileSpecBuilder = FileSpec.builder(escapeStdlibPackageName(kTypeElement.packageName()), kTypeElement.simpleName() + POSIX)
        val functionBuilder = FunSpec.builder("deepCopy")
            .receiver(kTypeElement.kotlinClassName)
            .addModifiers(KModifier.PUBLIC)
            .returns(kTypeElement.kotlinClassName)
            .addOriginatingElement(kTypeElement.typeElement)

        functionBuilder.addTypeVariables(kTypeElement.typeVariablesWithoutVariance)
            .addAnnotation(JvmOverloads::class)

        val suppressWarnings = hashSetOf<String>()

        val statementStringBuilder = StringBuilder("%T(")

        kTypeElement.components.forEach { component ->
            val kTypeElement = component.typeElement
            if (kTypeElement != null) {
                if (kTypeElement.canDeepCopy) {
                    fileSpecBuilder.addImport(
                        kTypeElement.escapedPackageName,
                        "deepCopy"
                    )

                    val nullableMark = if (component.type.isNullable) "?" else ""
                    statementStringBuilder.append("${component.name}${nullableMark}.deepCopy(), ")
                } else if (kTypeElement.isCollectionType) {
                    val elementType = component.typeArgumentElements.singleOrNull()
                    val method = if (elementType.isDeepCopiable()){
                        fileSpecBuilder.addImport(RUNTIME_PACKAGE, "deepCopy")
                        fileSpecBuilder.addImport(elementType.escapedPackageName, "deepCopy")
                        "deepCopy { it.deepCopy() }"
                    } else {
                        fileSpecBuilder.addImport(RUNTIME_PACKAGE, "copy")
                        "copy()"
                    }
                    val nullableMark = if (component.type.isNullable) "?" else ""
                    statementStringBuilder.append("${component.name}${nullableMark}.${method}, ")
                } else if (kTypeElement.isMapType) {
                    
                    val keyType = component.typeArgumentElements[0]
                    val valueType = component.typeArgumentElements[1]
                    val method = when {
                        keyType.isDeepCopiable() && valueType.isDeepCopiable() -> {
                            fileSpecBuilder.addImport(RUNTIME_PACKAGE, "deepCopy")
                            fileSpecBuilder.addImport(keyType.escapedPackageName, "deepCopy")
                            fileSpecBuilder.addImport(valueType.escapedPackageName, "deepCopy")
                            "deepCopy({ it.deepCopy() }, { it.deepCopy() })"
                        }
                        keyType.isDeepCopiable() && !valueType.isDeepCopiable() -> {
                            fileSpecBuilder.addImport(RUNTIME_PACKAGE, "deepCopy")
                            fileSpecBuilder.addImport(keyType.escapedPackageName, "deepCopy")
                            "deepCopy({ it.deepCopy() }, { it })"
                        }
                        !keyType.isDeepCopiable() && valueType.isDeepCopiable() -> {
                            fileSpecBuilder.addImport(RUNTIME_PACKAGE, "deepCopy")
                            fileSpecBuilder.addImport(valueType.escapedPackageName, "deepCopy")
                            "deepCopy({ it }, { it.deepCopy() })"
                        }
                        else -> {
                            fileSpecBuilder.addImport(RUNTIME_PACKAGE, "copy")
                            "copy()"
                        }
                    }
                    val nullableMark = if (component.type.isNullable) "?" else ""
                    statementStringBuilder.append("${component.name}${nullableMark}.${method}, ")
                } else {
                    statementStringBuilder.append("${component.name}, ")
                }
            } else {
                statementStringBuilder.append("${component.name}, ")
            }
            
            functionBuilder.addParameter(ParameterSpec.builder(component.name, component.type).defaultValue("this.${component.name}").build())
        }
        statementStringBuilder.setCharAt(statementStringBuilder.lastIndex - 1, ')')

        functionBuilder.addStatement("return $statementStringBuilder", 
            kTypeElement.kotlinClassName)

        suppressWarnings.forEach {
            functionBuilder.addAnnotation(AnnotationSpec.builder(Suppress::class).addMember("%S", it).build())
        }

        fileSpecBuilder.addFunction(functionBuilder.build()).build().writeTo(AptContext.filer)
    }
}