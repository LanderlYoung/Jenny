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
package io.github.landerlyoung.jenny;

import com.google.auto.service.AutoService;

import java.util.HashSet;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;

/**
 * Author: landerlyoung@gmail.com
 * Date:   2014-12-16
 * Time:   19:42
 * Life with passion. Code with creativity!
 */
@AutoService(Processor.class)
public class JennyAnnotationProcessor extends AbstractProcessor {
    private Messager mMessager;
    private Types mTypeUtils;
    private Elements mElementsUtils;
    private Filer mFiler;

    private static final Set<String> SUPPORTED_ANNOTATIONS;

    static {
        SUPPORTED_ANNOTATIONS = new HashSet<>();
        SUPPORTED_ANNOTATIONS.add(NativeClass.class.getName());
        SUPPORTED_ANNOTATIONS.add(NativeCode.class.getName());
        SUPPORTED_ANNOTATIONS.add(NativeProxy.class.getName());
        SUPPORTED_ANNOTATIONS.add(NativeMethodProxy.class.getName());
    }

    public JennyAnnotationProcessor() {
    }

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        mMessager = processingEnv.getMessager();
        mTypeUtils = processingEnv.getTypeUtils();
        mElementsUtils = processingEnv.getElementUtils();
        mFiler = processingEnv.getFiler();
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        if (roundEnv.errorRaised() || roundEnv.processingOver()) return false;

        generateNativeGlueCode(roundEnv);
        generateNativeReflect(roundEnv);

        return true;
    }

    private boolean generateNativeGlueCode(RoundEnvironment roundEnv) {
        //classify annotations by class
        Set<? extends Element> classes = roundEnv.getElementsAnnotatedWith(NativeClass.class);
        if (classes.isEmpty()) return false;

        Environment env = new Environment(mMessager,
                mTypeUtils, mElementsUtils, mFiler, roundEnv);
        classes.parallelStream()
               .filter(ec -> ec instanceof TypeElement)
               .forEach(ec -> new CppGlueCodeGenerator(env, (TypeElement) ec).doGenerate());
        return true;
    }

    private boolean generateNativeReflect(RoundEnvironment roundEnv) {
        Set<? extends Element> classes = roundEnv.getElementsAnnotatedWith(NativeProxy.class);
        if (classes.isEmpty()) return false;

        Environment env = new Environment(mMessager,
                mTypeUtils, mElementsUtils, mFiler, roundEnv);
        classes.parallelStream()
               .filter(ec -> ec instanceof TypeElement)
               .forEach(ec -> new NativeProxyCodeGenerator(env, (TypeElement) ec).doGenerate());
        return false;
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        return SUPPORTED_ANNOTATIONS;
    }
}
