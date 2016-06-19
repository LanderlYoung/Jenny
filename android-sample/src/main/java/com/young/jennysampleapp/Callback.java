package com.young.jennysampleapp;

import com.young.jenny.annotation.NativeFieldProxy;
import com.young.jenny.annotation.NativeMethodProxy;
import com.young.jenny.annotation.NativeProxy;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;

/**
 * Author: landerlyoung@gmail.com
 * Date:   2016-06-02
 * Time:   19-58
 * Life with Passion, Code with Creativity.
 */
@NativeProxy(allFields = true, allMethods = false)
public class Callback {

    protected Object lock;

    public static final int COMPILE_CONSTANT_INT = 15;

    @NativeFieldProxy(setter = true)
    public final int ANOTHER_COMPILE_CONSTANT_INT = 16;

    public int count = 160;
    public static int staticCount = 10;

    public String name = "callback";



    public static String staticName = "static";

    public static List<String> aStaticField = Collections.emptyList();

    @NativeMethodProxy
    public Callback() {
    }

    @NativeMethodProxy
    public Callback(int a) {

    }

    @NativeMethodProxy
    public Callback(HashMap<?, ?> sth) {

    }

    @NativeProxy
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

    @NativeMethodProxy
    void onJobDone(boolean success, String result) {

    }

    @NativeMethodProxy
    void onJobProgress(long progress) {

    }

    @NativeMethodProxy
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
