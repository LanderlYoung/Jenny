/**
 * Copyright 2016 landerlyoung@gmail.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.github.landerlyoung.jenny

import java.io.File
import java.util.*
import java.io.OutputStream
import javax.annotation.processing.Filer
import javax.annotation.processing.Messager
import javax.lang.model.util.Elements
import javax.lang.model.util.Types
import javax.tools.Diagnostic
import javax.tools.StandardLocation

/**
 * Author: landerlyoung@gmail.com
 * Date:   2014-12-18
 * Time:   10:02
 * Life with passion. Code with creativity!
 */
class Environment(
        val messager: Messager,
        val typeUtils: Types,
        val elementUtils: Elements,
        private val filer: Filer,
        val configurations: Configurations
) {

    fun createOutputFile(packageName: String, name: String): OutputStream =
            configurations.outputDirectory?.let {
                File(it, packageName.replace('.', File.separatorChar) + File.separatorChar + name)
                        .apply { parentFile.mkdirs() }
                        .outputStream().buffered()
            } ?: filer.createResource(StandardLocation.SOURCE_OUTPUT, packageName, name).openOutputStream()

}
