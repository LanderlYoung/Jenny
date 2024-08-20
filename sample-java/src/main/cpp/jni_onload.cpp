
#include <jni.h>
#include "ComputeInNative.h"
#include "jnihelper.h"
#include "jenny_fusion_proxies.h"

JNIEXPORT jint JNI_OnLoad(JavaVM *vm, void *reserved) {
    JNIEnv* env;
    if (vm->GetEnv(reinterpret_cast<void**>(&env),
                   JNI_VERSION_1_6) != JNI_OK) {
        return -1;
    }
    ::jenny::Env::attachJvm(vm);

    auto ok = ComputeInNative::registerNativeFunctions(env) &&
        jenny::initAllProxies(env);

    assert(ok);

    return JNI_VERSION_1_6;
}