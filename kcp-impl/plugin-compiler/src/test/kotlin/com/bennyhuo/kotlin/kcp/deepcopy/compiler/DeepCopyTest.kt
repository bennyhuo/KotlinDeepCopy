/*
 * Copyright (C) 2020 Brian Norman
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.bennyhuo.kotlin.kcp.deepcopy.compiler

import com.tschuchort.compiletesting.KotlinCompilation
import com.tschuchort.compiletesting.SourceFile
import org.jetbrains.kotlin.compiler.plugin.ComponentRegistrar
import org.junit.Test
import kotlin.test.assertEquals

class IrPluginTest {
    @Test
    fun `IR plugin success`() {
        val result = compile(
            sourceFiles =
            listOf(
                SourceFile.java(
                    "DeepCopy.java",
                    """
                    package com.bennyhuo.kotlin.deepcopy.annotations;
    
                    public @interface DeepCopy {
                    }
                    """.trimIndent()
                ),
                SourceFile.kotlin(
                    "main.kt",
                    """
                    import com.bennyhuo.kotlin.deepcopy.annotations.DeepCopy

                    @DeepCopy
                    data class DataClass(val name: String)

                    class PlainClass(val name: String)
                    """.trimIndent()
                )
            )
        )
        assertEquals(KotlinCompilation.ExitCode.OK, result.exitCode)
    }
}

fun compile(
    sourceFiles: List<SourceFile>,
    plugin: ComponentRegistrar = DeepCopyComponentRegistrar(),
): KotlinCompilation.Result {
    return KotlinCompilation().apply {
        sources = sourceFiles
        useIR = true
        compilerPlugins = listOf(plugin)
        inheritClassPath = true
    }.compile()
}

fun compile(
    sourceFile: SourceFile,
    plugin: ComponentRegistrar = DeepCopyComponentRegistrar(),
): KotlinCompilation.Result {
    return compile(listOf(sourceFile), plugin)
}
