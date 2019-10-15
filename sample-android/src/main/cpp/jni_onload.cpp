/**
* <pre>
* Author: landerlyoung@gmail.com
* Date:   2019-09-26
* Time:   15:32
* Life with Passion, Code with Creativity.
* </pre>
*/

#include <jni.h>
#include <cassert>
#include <android/log.h>
#include "ComputeIntensiveClass.h"
#include "NestedNativeClass.h"
#include "NativeDrawable.h"

JNIEXPORT jint JNI_OnLoad(JavaVM *vm, void *reserved) {
    JNIEnv *env;
    if (vm->GetEnv(reinterpret_cast<void**>(&env),
                   JNI_VERSION_1_6) != JNI_OK) {
        return -1;
    }
    assert(ComputeIntensiveClass::registerNativeFunctions(env));
    assert(NativeDrawable::registerNativeFunctions(env));
    return JNI_VERSION_1_6;
}

void jennySampleErrorLog(const char *error) {
    __android_log_assert(error, "jenny", nullptr);
}