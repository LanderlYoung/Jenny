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

import javax.annotation.processing.AbstractProcessor
import javax.annotation.processing.Filer
import javax.annotation.processing.Messager
import javax.annotation.processing.ProcessingEnvironment
import javax.annotation.processing.RoundEnvironment
import javax.lang.model.SourceVersion
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
    private lateinit var mConfigurations: Configurations

    @Synchronized
    override fun init(processingEnv: ProcessingEnvironment) {
        super.init(processingEnv)
        mMessager = processingEnv.messager
        mTypeUtils = processingEnv.typeUtils
        mElementsUtils = processingEnv.elementUtils
        mFiler = processingEnv.filer
        mConfigurations = Configurations.fromOptions(processingEnv.options)
    }

    override fun process(annotations: Set<TypeElement>, roundEnv: RoundEnvironment): Boolean {
        if (roundEnv.errorRaised()
                || roundEnv.processingOver()
                || !annotations.any { it.qualifiedName.toString() in SUPPORTED_ANNOTATIONS }) return false

        generateNativeGlueCode(roundEnv)
        generateNativeProxy(roundEnv)

        return true
    }

    private fun generateNativeGlueCode(roundEnv: RoundEnvironment) {
        // classify annotations by class
        val classes = roundEnv.getElementsAnnotatedWith(NativeClass::class.java)

        val env = Environment(mMessager, mTypeUtils, mElementsUtils, mFiler, mConfigurations)
        classes.asSequence()
                .filter { it is TypeElement }
                .forEach { NativeGlueGenerator(env, it as TypeElement).doGenerate() }
    }

    private fun generateNativeProxy(roundEnv: RoundEnvironment) {
        val env = Environment(mMessager, mTypeUtils, mElementsUtils, mFiler, mConfigurations)

        roundEnv.getElementsAnnotatedWith(NativeProxy::class.java)
                .forEach {
                    val config = NativeProxyGenerator.NativeProxyConfig(
                            (it.getAnnotation(NativeProxy::class.java)
                                    ?: AnnotationResolver.getDefaultImplementation(NativeProxy::class.java)))
                    NativeProxyGenerator(env, it as TypeElement, config).doGenerate()
                }

        (roundEnv.getElementsAnnotatedWith(NativeProxyForClasses::class.java)
                .asSequence()
                .map { it.getAnnotation(NativeProxyForClasses::class.java) }
                +
                roundEnv.getElementsAnnotatedWith(NativeProxyForClasses.RepeatContainer::class.java)
                        .asSequence()
                        .flatMap { it.getAnnotationsByType(NativeProxyForClasses::class.java).asSequence() }
                )
                .forEach { annotation ->
                    try {
                        annotation.classes
                        throw AssertionError("unreachable")
                    } catch (e: MirroredTypesException) {
                        e.typeMirrors
                    }.forEach {
                        val clazz = mTypeUtils.asElement(it) as TypeElement

                        val config = NativeProxyGenerator.NativeProxyConfig(
                                allMethods = true, allFields = true, namespace = annotation.namespace, onlyPublic = true)

                        NativeProxyGenerator(env, clazz, config).doGenerate()
                    }
                }
    }

    override fun getSupportedSourceVersion(): SourceVersion {
        return SourceVersion.latestSupported()
    }

    override fun getSupportedAnnotationTypes(): Set<String> {
        return SUPPORTED_ANNOTATIONS
    }

    override fun getSupportedOptions(): Set<String> {
        return SUPPORTED_OPTIONS
    }

    companion object {
        private val SUPPORTED_ANNOTATIONS: Set<String> = setOf(
                NativeClass::class.java.name,
                NativeCode::class.java.name,
                NativeFieldProxy::class.java.name,
                NativeMethodProxy::class.java.name,
                NativeProxy::class.java.name,
                NativeProxyForClasses::class.java.name
        )

        private val SUPPORTED_OPTIONS: Set<String> = setOf(
                Configurations.ERROR_LOGGER_FUNCTION,
                Configurations.THREAD_SAFE
        )
    }
}
