package com.young.jennysampleapp;

import com.young.jenny.annotation.NativeClass;
import com.young.jenny.annotation.NativeMethod;

/**
 * Author: taylorcyang@tencent.com
 * Date:   2016-05-24
 * Time:   20-50
 * Life with Passion, Code with Creativity.
 */
@NativeClass
public class ComputeIntensiveClass {

    @NativeMethod("return a + b;")
    public native int addInNative(int a, int b);

    @NativeMethod
    public void hello() {

    }
}
