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

import io.github.landerlyoung.jenny.NativeClass;
import io.github.landerlyoung.jenny.NativeCode;
import io.github.landerlyoung.jenny.NativeProxyForClasses;
import java.util.HashMap;
import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

/**
 * Author: landerlyoung@gmail.com
 * Date:   2016-05-24
 * Time:   20-50
 * Life with Passion, Code with Creativity.
 */
@NativeClass(/*namespace = "io::github::landerlyoung"*/)
public class ComputeIntensiveClass {

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

    public static native void testOverload();
    public static native void testOverload(int i);

    // showcase for native proxy
    @NativeProxyForClasses(
            namespace = "java::okhttp", classes = {
            OkHttpClient.class, Request.class, Request.Builder.class,
            Call.class, Response.class, ResponseBody.class
    })
    public static native String httpGet(String url);

    public native int computeThenCallback(Callback listener);

    public static native void runJniHelperTest();

    // apply NativeCode annotation to non-native method
    // will raise a compile time error
    //@NativeCode
    public void hello() {

    }

    @NativeProxyForClasses(
            namespace = "java::lang", classes = {
            RuntimeException.class
    })
    private int dummy;

    @NativeClass(dynamicRegisterJniMethods = false)
    public static class NestedNativeClass {

        public static final int CONST = 0;

        public native HashMap<String, String> one(String param);

        public native long nativeInit();

        public native void testOverload();

        public native void testOverload(Object o);

        public native void nativeRelease(long handle);
    }
}
