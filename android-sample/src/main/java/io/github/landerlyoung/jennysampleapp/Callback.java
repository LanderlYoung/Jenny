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

import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import io.github.landerlyoung.jenny.NativeFieldProxy;
import io.github.landerlyoung.jenny.NativeMethodProxy;
import io.github.landerlyoung.jenny.NativeProxy;

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
    public void onJobStart() {

    }

    @NativeMethodProxy
    void onJobStart(NestedClass overrloadedMethod) {

    }

    @NativeMethodProxy
    public static void newInstnace() {

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
