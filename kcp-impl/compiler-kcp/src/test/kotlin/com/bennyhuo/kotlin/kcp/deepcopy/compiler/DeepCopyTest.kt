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

import com.bennyhuo.kotlin.compiletesting.extensions.module.KotlinModule
import com.bennyhuo.kotlin.compiletesting.extensions.module.compileAll
import com.bennyhuo.kotlin.compiletesting.extensions.module.resolveAllDependencies
import com.bennyhuo.kotlin.compiletesting.extensions.result.ResultCollector
import com.bennyhuo.kotlin.compiletesting.extensions.source.SingleFileModuleInfoLoader
import org.junit.Test

class DeepCopyTest {
    @Test
    fun basic() {
        testBase("basic.kt")
    }

    private fun testBase(fileName: String) {
        val loader = SingleFileModuleInfoLoader("testData/$fileName")
        val sourceModuleInfos = loader.loadSourceModuleInfos()

        val modules = sourceModuleInfos.map {
            KotlinModule(it, componentRegistrars = listOf(DeepCopyComponentRegistrar()))
        }

        modules.resolveAllDependencies()
        modules.compileAll()

        val resultMap = modules.associate {
            it.name to it.runJvm()
        }

        loader.loadExpectModuleInfos().fold(ResultCollector()) { collector, expectModuleInfo ->
            collector.collectModule(expectModuleInfo.name)
            expectModuleInfo.sourceFileInfos.forEach {
                collector.collectFile(it.fileName)
                collector.collectLine(it.sourceBuilder, resultMap[expectModuleInfo.name]?.get(it.fileName))
            }
            collector
        }.apply()
    }
}