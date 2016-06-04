package com.young.util.jni.generator;

import javax.lang.model.element.TypeElement;

/**
 * Author: landerlyoung@tencent.com
 * Date:   2016-06-05
 * Time:   00:30
 * Life with Passion, Code with Creativity.
 */
public class NativeReflectCodeGenerator extends AbsCodeGenerator {
    public NativeReflectCodeGenerator(Environment env, TypeElement clazz) {
        super(env, clazz);
    }

    public void doGenerate() {
        log("xxx type = " + mClassName);
    }
}
