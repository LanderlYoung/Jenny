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

import javax.annotation.processing.*
import javax.lang.model.SourceVersion
import javax.lang.model.element.Element
import javax.lang.model.element.TypeElement
import javax.lang.model.type.MirroredTypesException
import javax.lang.model.util.Elements
import javax.lang.model.util.Types

/**
 * Author: landerlyoung@gmail.com
 * Date:   2014-12-16
 * Time:   19:42
 * Life with passion. Code with creativity!
 */
class JennyAnnotationProcessor : AbstractProcessor() {
    private lateinit var mMessager: Messager
    private lateinit var mTypeUtils: Types
    private lateinit var mElementsUtils: Elements
    private lateinit var mFiler: Filer

    @Synchronized
    override fun init(processingEnv: ProcessingEnvironment) {
        super.init(processingEnv)
        mMessager = processingEnv.messager
        mTypeUtils = processingEnv.typeUtils
        mElementsUtils = processingEnv.elementUtils
        mFiler = processingEnv.filer
    }

    override fun process(annotations: Set<TypeElement>, roundEnv: RoundEnvironment): Boolean {
        if (roundEnv.errorRaised() || roundEnv.processingOver()) return false

        generateNativeGlueCode(roundEnv)
        generateNativeProxy(roundEnv)

        return true
    }

    private fun generateNativeGlueCode(roundEnv: RoundEnvironment): Boolean {
        // classify annotations by class
        val classes = roundEnv.getElementsAnnotatedWith(NativeClass::class.java)
        if (classes.isEmpty()) return false

        val env = Environment(mMessager, mTypeUtils, mElementsUtils, mFiler, roundEnv)
        classes.stream()
                .filter { ec -> ec is TypeElement }
                .forEach { ec -> CppGlueCodeGenerator(env, ec as TypeElement).doGenerate() }
        return true
    }

    private fun generateNativeProxy(roundEnv: RoundEnvironment): Boolean {
        @Suppress("UNCHECKED_CAST")
        val classes: MutableSet<Element> = roundEnv.getElementsAnnotatedWith(NativeProxy::class.java) as MutableSet<Element>
        classes.addAll(getProxyForExternalClasses(roundEnv))
        if (classes.isEmpty()) return false

        val env = Environment(mMessager, mTypeUtils, mElementsUtils, mFiler, roundEnv)
        classes.stream()
                .filter { ec -> ec is TypeElement }
                .forEach { ec -> NativeProxyCodeGenerator(env, ec as TypeElement).doGenerate() }
        return false
    }

    private fun getProxyForExternalClasses(roundEnv: RoundEnvironment): Collection<TypeElement> =
            roundEnv.getElementsAnnotatedWith(NativeProxyForClass::class.java)
                    .map {
                        try {
                            it.getAnnotation(NativeProxyForClass::class.java).classes
                            throw AssertionError("unreachable")
                        } catch (e: MirroredTypesException) {
                            e.typeMirrors
                        }
                    }.flatMap { it.map { mTypeUtils.asElement(it) as TypeElement } }

    override fun getSupportedSourceVersion(): SourceVersion {
        return SourceVersion.latestSupported()
    }

    override fun getSupportedAnnotationTypes(): Set<String> {
        return SUPPORTED_ANNOTATIONS
    }

    companion object {
        private val SUPPORTED_ANNOTATIONS: Set<String> = setOf(
                NativeClass::class.java.name,
                NativeCode::class.java.name,
                NativeProxy::class.java.name,
                NativeMethodProxy::class.java.name)

    }
}
