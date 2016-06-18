package com.young.jennysampleapp;

import com.young.jenny.annotation.NativeAccessField;
import com.young.jenny.annotation.NativeReflect;
import com.young.jenny.annotation.NativeReflectMethod;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

/**
 * Author: landerlyoung@gmail.com
 * Date:   2016-06-02
 * Time:   19-58
 * Life with Passion, Code with Creativity.
 */
@NativeReflect(allFields = false, allMethods = false)
public class Callback {

    protected Object lock;

    public static final int COMPILE_CONSTANT_INT = 15;
    public final int ANOTHER_COMPILE_CONSTANT_INT = 16;

    public int count = 160;
    public static int staticCount = 10;

    @NativeAccessField(auto = true)
    public String name = "callback";


    public static String staticName = "static";

    public static List<String> aStaticField = Collections.emptyList();

    @NativeReflectMethod
    public Callback() {
    }

    @NativeReflectMethod
    public Callback(int a) {

    }

    @NativeReflectMethod
    public Callback(HashMap<?, ?> sth) {

    }

    @NativeReflect
    public class NestedClass {
        public void hello() {
            int a = COMPILE_CONSTANT_INT;
            int b = count;
            int c = a + b;
        }
    }

    public static int aStaticMethod() {
        return 0;
    }

    @NativeReflectMethod
    void onJobDone(boolean success, String result) {

    }

    @NativeReflectMethod
    void onJobProgress(long progress) {

    }

    @NativeReflectMethod
    void onJobStart() {

    }

    public static Class<Callback> getMyClass() {
        return Callback.class;
    }

    int prepareRun() {
        return 0;
    }


    public void setName(String name) {
        this.name = name.toUpperCase();
    }
}
