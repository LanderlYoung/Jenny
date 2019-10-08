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
package io.github.landerlyoung.jennysample;

import java.util.Map;

import io.github.landerlyoung.jenny.NativeClass;
import io.github.landerlyoung.jenny.NativeFieldProxy;
import io.github.landerlyoung.jenny.NativeProxy;

/**
 * Author: landerlyoung@gmail.com
 * Date:   2016-06-19
 * Time:   21:51
 * Life with Passion, Code with Creativity.
 */

/**add this annotation to generate native proxy class
 so we chan access the mNativeContext field with ease.
 and also, disable all field getter/setter and method proxy,
 except for those we explicitly annotated with {@link NativeFieldProxy}
 */
@NativeProxy(allFields = false, allMethods = false)
//java project should disable android log marcos
@NativeClass()
public class ComputeInNative {
    static {
        System.loadLibrary("jnilib");
    }

    @NativeFieldProxy
    private long nativeContext;

    public ComputeInNative() {

    }

    public native boolean init();

    public native void release();

    public native void setParam(Map<String, String> globalHttpParam);

    public native Map<String, String> getGlobalParam();

    public native boolean request(String json, RequestListener listener);
}
