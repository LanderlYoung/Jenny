package com.young.util.jni.generator;

import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;

/**
 * Author: landerlyoung@gmail.com
 * Date:   2016-06-05
 * Time:   00:42
 * Life with Passion, Code with Creativity.
 */
public abstract class AbsCodeGenerator {
    private static final String LOG_PREFIX = "Jenny | ";

    protected final Environment mEnv;
    protected final TypeElement mClazz;
    protected final HandyHelper mHelper;

    public static final String PKG_NAME = "jenny";

    /** like com.example_package.SomeClass$InnerClass */
    protected final String mClassName;

    /**
     *  like com_example_1package_SomeClass_InnerClass
     *  NestedClass
     *  com.young.jennysampleapp.ComputeIntensiveClass$NestedNativeClass
     *  com_young_jennysampleapp_ComputeIntensiveClass_00024NestedNativeClass
     */
    protected final String mJNIClassName;

    /** like com/example_package/SomeClass$InnerClass */
    protected final String mSlashClassName;

    /** like String for java.lang.String class */
    protected final String mSimpleClassName;

    public AbsCodeGenerator(Environment env, TypeElement clazz) {
        mEnv = env;

        if (clazz.getKind() != ElementKind.CLASS
                && clazz.getKind() != ElementKind.INTERFACE
                && clazz.getKind() != ElementKind.ENUM
                && clazz.getKind() != ElementKind.ANNOTATION_TYPE) {
            error("type element " + clazz.toString() + " is not class type");
        }
        mClazz = clazz;
        mHelper = new HandyHelper(env);

        mClassName = mHelper.getClassName(mClazz);
        mJNIClassName = mHelper.toJNIClassName(mClassName);
        mSlashClassName = mHelper.getSlashClassName(mClassName);
        mSimpleClassName = mHelper.getSimpleName(mClazz);
    }

    public abstract void doGenerate();

    public void log(String msg) {
        mEnv.messager.printMessage(Diagnostic.Kind.NOTE, LOG_PREFIX + msg);
    }

    public void warn(String msg) {
        mEnv.messager.printMessage(Diagnostic.Kind.WARNING, LOG_PREFIX + msg);
    }

    public void error(String msg) {
        mEnv.messager.printMessage(Diagnostic.Kind.ERROR, LOG_PREFIX + msg);
    }
}
