package com.young.util.jni.generator;

import com.young.util.jni.JNIHelper;

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
    protected final Environment mEnv;
    protected final TypeElement mClazz;
    protected final HandyHelper mHelper;

    //like com.example_package.SomeClass$InnerClass
    protected String mClassName;
    //like com_example_1package_SomeClass_InnerClass
    protected String mJNIClassName;
    //like com/example_package/SomeClass$InnerClass
    protected String mSlashClassName;

    public AbsCodeGenerator(Environment env, TypeElement clazz) {
        if (clazz.getKind() != ElementKind.CLASS) {
            throw new IllegalArgumentException("type element clazz is not class type");
        }
        mEnv = env;
        mClazz = clazz;
        mHelper = new HandyHelper(env);

        mClassName = mHelper.getClassName(mClazz);
        mJNIClassName = JNIHelper.toJNIClassName(mClassName);
        mSlashClassName = JNIHelper.getNativeSlashClassName(mClassName);
    }

    public abstract void doGenerate();


    public void log(String msg) {
        mEnv.messager.printMessage(Diagnostic.Kind.NOTE, msg);
    }

    public void warn(String msg) {
        mEnv.messager.printMessage(Diagnostic.Kind.WARNING, msg);
    }

    public void error(String msg) {
        mEnv.messager.printMessage(Diagnostic.Kind.ERROR, msg);
    }
}
