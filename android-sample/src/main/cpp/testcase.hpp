//
// Created by landerlyoung on 8/3/16.
//

#include "CallbackProxy.hpp"
#include "NestedClassProxy.hpp"

jint testcase(JNIEnv *env, jobject thiz, jobject listener) {
    LOGV("Hello world");
    CallbackProxy callback(env, listener, false);
    callback.onJobStart(env);
    callback.getName(env);

    jstring name = (CallbackProxy(env, listener, false)).getName(env);

    jobject newInstance = CallbackProxy::newInstance(env);
    callback.setLock(env, newInstance);
    callback.onJobProgress(env, 20);

    jobject nestedClass = NestedClassProxy::newInstance(env, listener);
    callback.setLock(env, nestedClass);
    callback.onJobProgress(env, 50);

    LOGV("staticField=%p", callback.getAStaticField(env));
    callback.setAStaticField(env, nullptr);
    LOGV("set staticField=%p", callback.getAStaticField(env));

    callback.setCount(env, 100);
    LOGV("count=%d", callback.getCount(env));
    callback.setLock(env, listener);
    callback.onJobProgress(env, 100);

    LOGV("get CMPILE_TIME_CONSTNT %d", callback.COMPILE_CONSTANT_INT);

    callback.onJobDone(env, JNI_TRUE, env->NewStringUTF("Yes, callback from jni"));
    return 0;
}
