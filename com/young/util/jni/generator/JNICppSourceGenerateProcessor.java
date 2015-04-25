package com.young.util.jni.generator;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import java.util.Set;
import java.util.TreeSet;

/**
 * Author: LanderlYoung
 * Date:   2014-12-16
 * Time:   19:42
 * Life with passion. Code with creativity!
 */

public class JNICppSourceGenerateProcessor extends AbstractProcessor {
    private Messager mMessager;
    private Types mTypeUtils;
    private Elements mElementsUtils;
    private Filer mFiler;

    private static final Set<String> SUPPORTED_ANNOTATIONS;

    static {
        SUPPORTED_ANNOTATIONS = new TreeSet<String>();
        SUPPORTED_ANNOTATIONS.add(NativeClass.class.getName());
        SUPPORTED_ANNOTATIONS.add(NativeSource.class.getName());
    }

    public JNICppSourceGenerateProcessor() {
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
        if (roundEnv.errorRaised() || roundEnv.processingOver()) return true;

        //classify annotations by class
        Set<? extends Element> classes =
                roundEnv.getElementsAnnotatedWith(NativeClass.class);

        if (classes.isEmpty()) return true;

        final Environment env = new Environment(mMessager,
                mTypeUtils, mElementsUtils, mFiler, roundEnv);
        for (Element ec : classes) {
            if (ec instanceof TypeElement) {
                CppCodeGenerator codeGen = new CppCodeGenerator(env, (TypeElement) ec);
                codeGen.doGenerate();
            }
        }
        return true;
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
