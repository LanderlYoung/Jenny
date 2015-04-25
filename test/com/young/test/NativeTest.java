/*************************************************************************
  > File Name:     com/young/test/NativeTest.java
  > Author:        Landerl Young
  > Mail:          LanderlYoung@gmail.com
  > Created Time:  2015年04月25日 星期六 15时43分30秒
 ************************************************************************/
package com.young.test;

import com.young.util.jni.generator.NativeClass;
import com.young.util.jni.generator.NativeSource;

@NativeClass
public class NativeTest {
    static {
        System.loadLibrary("native_lib");
    }

    @NativeSource(
    "jint c = a + b;\n"+
    "return c;")
    public native int add(int a, int b);

    public native void cpp_magic(String s, RuntimeException re);

    public static void main(String[] args) {
        NativeTest n = new NativeTest();
        NativeInnerClass nic = new NativeInnerClass();
        System.out.println(n.add(1, 2));
        System.out.println(nic.sub(2, 1));
    }

    @NativeClass()
    public static class NativeInnerClass {

        @NativeSource("return a - b;")
        public native int sub(int a, int b);
    }
}
