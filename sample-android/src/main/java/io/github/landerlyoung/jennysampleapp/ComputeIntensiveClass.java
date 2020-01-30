/**
 * Copyright 2016 landerlyoung@gmail.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.github.landerlyoung.jennysampleapp;

import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.Collections;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;

import io.github.landerlyoung.jenny.NativeClass;
import io.github.landerlyoung.jenny.NativeCode;
import io.github.landerlyoung.jenny.NativeProxyForClasses;

/**
 * Author: landerlyoung@gmail.com
 * Date:   2016-05-24
 * Time:   20-50
 * Life with Passion, Code with Creativity.
 */
@NativeClass(/*namespace = "io::github::landerlyoung"*/)
@NativeProxyForClasses(
        namespace = "java", classes = {
        InputStream.class, String.class, URL.class, URLConnection.class,
})
public class ComputeIntensiveClass {

    @NativeProxyForClasses(
            namespace = "test::java::lang", classes = {
            File.class, InputStream.class,
            String.class, URL.class, URLConnection.class,
            Collections.class, ReentrantLock.class,
            AtomicInteger.class, Thread.class
    })
    private class Test {
    }

    static {
        System.loadLibrary("hello-jenny");
    }

    public static final String LOG_TAG = "ComputeIntensiveClass";

    public static final String KEY_WHERE_ARE_YOUT_FROM = "where_are_you_from";

    public static final int IDEL = -1;
    public static final int BUSY = 1;

    @NativeCode({
            "jint c = a + b;",
            "return c;"
    })
    public native int addInNative(int a, int b);

    public static native void computeSomething(byte[] sth);

    public static native String greet();

    public final native void testParamParse(int a,
                                      String b,
                                      long[] c,
                                      float[][] d,
                                      Exception e,
                                      Class<String> f,
                                      HashMap<?, ?> g);

    public static native long returnsLong();

    public static native boolean returnsBool();

    public static native Object returnsObject();

    // showcase for native proxy
    public static native String httpGet(String url);

    public native int computeThenCallback(Callback listener);

    // apply NativeCode annotation to non-native method
    // will raise a compile time error
    //@NativeCode
    public void hello() {

    }

    @NativeClass(dynamicRegisterJniMethods = false)
    public static class NestedNativeClass {
        public static final int CONST = 0;

        public native HashMap<String, String> one(String param);

        public native long nativeInit();

        public native void nativeRelease(long handle);
    }
}
