package com.young.jenny;

import com.young.jenny.annotation.NativeClass;
import com.young.jenny.annotation.NativeFieldProxy;
import com.young.jenny.annotation.NativeProxy;

import java.util.Map;

/**
 * Author: taylorcyang@tencent.com
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
@NativeClass(androidLog = false)
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
