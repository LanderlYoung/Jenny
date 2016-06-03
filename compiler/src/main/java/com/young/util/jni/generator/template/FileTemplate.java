package com.young.util.jni.generator.template;

/**
 * Author: taylorcyang@tencent.com
 * Date:   2016-06-03
 * Time:   00:42
 * Life with Passion, Code with Creativity.
 */
public enum FileTemplate {
    JNI_CPP_TEMPLATE("jni_cpp_template.cpp"),
    JNI_HEADER_TEMPLATE("jni_header_template.h"),
    JNINATIVEMETHOD_STRUCT_TEMPLATE("JNINativeMethodStruct_template.cpp"),
    NATIVE_METHOD_DECLARE_TEMPLATE("native_method_declare_template.h"),
    NATIVE_METHOD_TEMPLATE("native_method_template.cpp"),
    CONSTANT_TEMPLATE("constant_template.h");

    private String mName;

    FileTemplate(String name) {
        mName = name;
    }

    public String getName() {
        return mName;
    }
    }
