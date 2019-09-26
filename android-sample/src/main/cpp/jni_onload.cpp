/**
* <pre>
* Author: taylorcyang@tencent.com
* Date:   2019-09-26
* Time:   15:32
* Life with Passion, Code with Creativity.
* </pre>
*/

#include <jni.h>
#include <ComputeIntensiveClass.h>
#include <NestedNativeClass.h>

JNIEXPORT jint JNICALL JNI_OnLoad(JavaVM *vm, void *reserved) {
    JNIEnv *env;
    if (vm->GetEnv(reinterpret_cast<void**>(&env),
                   JNI_VERSION_1_6) != JNI_OK) {
        return -1;
    }
    ComputeIntensiveClass::registerNativeFunctions(env);
    return JNI_VERSION_1_6;
}

