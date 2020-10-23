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
#include "gen/GenericProxy.h"
#include "gen/jnihelper.h"
#include "gen/jenny_fusion_proxies.h"

#include "gen/java_okhttp_BuilderProxy.h"
#include "gen/java_okhttp_OkHttpClientProxy.h"
#include "gen/java_okhttp_RequestProxy.h"
#include "gen/java_okhttp_CallProxy.h"
#include "gen/java_okhttp_ResponseProxy.h"
#include "gen/java_okhttp_ResponseBodyProxy.h"

JNIEXPORT jint JNI_OnLoad(JavaVM *vm, void *reserved) {
    JNIEnv* env;
    if (vm->GetEnv(reinterpret_cast<void**>(&env),
                   JNI_VERSION_1_6) != JNI_OK) {
        return -1;
    }
    ::jenny::Env::attachJvm(vm);

    auto ok = ComputeIntensiveClass::registerNativeFunctions(env) &&
        NativeDrawable::registerNativeFunctions(env) &&
        // test generic init success
        jenny::initAllProxies(env);

    assert(ok);

    return JNI_VERSION_1_6;
}

void jennySampleErrorLog(JNIEnv *env, const char *error) {
    __android_log_write(ANDROID_LOG_ERROR, "jenny", error);
    env->ExceptionDescribe();
}