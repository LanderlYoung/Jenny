package com.young.jennysampleapp;

import com.young.jenny.annotation.NativeClass;
import com.young.jenny.annotation.NativeCode;

import java.util.HashMap;

/**
 * Author: taylorcyang@tencent.com
 * Date:   2016-05-24
 * Time:   20-50
 * Life with Passion, Code with Creativity.
 */
@NativeClass
public class ComputeIntensiveClass {
    static {
        System.loadLibrary("hello-jenny");
    }

    public static final String LOG_TAG = "ComputeIntensiveClass";

    public static final String KEY_WHERE_ARE_YOUT_FROM = "where_are_you_from";

    public static final int IDEL = -1;
    public static final int BUSY = 1;

    @NativeCode("return a + b;")
    public native int addInNative(int a, int b);

    public static native void computeSomething(byte[] sth);

    public final native void testParamParse(int a,
                                      String b,
                                      long[] c,
                                      float[][] d,
                                      Exception e,
                                      Class<String> f,
                                      HashMap<?, ?> g);

    // apply NativeCode annotation to non-native method
    // will raise a compilation time error
    //@NativeCode
    public void hello() {

    }
}
