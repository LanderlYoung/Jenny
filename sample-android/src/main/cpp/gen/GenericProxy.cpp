/**
 * File generated by Jenny -- https://github.com/LanderlYoung/Jenny
 *
 * DO NOT EDIT THIS FILE.
 *
 * For bug report, please refer to github issue tracker https://github.com/LanderlYoung/Jenny/issues,
 * or contact author landerlyoung@gmail.com.
 */
#include "GenericProxy.h"


// external logger function passed by jenny.errorLoggerFunction
void jennySampleErrorLog(JNIEnv* env, const char* error);




jclass GenericProxy::sClazz = nullptr;

// thread safe init
std::mutex GenericProxy::sInitLock;
std::atomic_bool GenericProxy::sInited;

/*static*/ bool GenericProxy::initClazz(JNIEnv *env) {
#define JENNY_CHECK_NULL(val)                      \
       do {                                        \
           if ((val) == nullptr) {                 \
               jennySampleErrorLog(env, "can't init GenericProxy::" #val); \
               return false;                       \
           }                                       \
       } while(false)

    if (!sInited) {
        std::lock_guard<std::mutex> lg(sInitLock);
        if (!sInited) {
            auto clazz = env->FindClass(FULL_CLASS_NAME);
            JENNY_CHECK_NULL(clazz);
            sClazz = reinterpret_cast<jclass>(env->NewGlobalRef(clazz));
            env->DeleteLocalRef(clazz);
            JENNY_CHECK_NULL(sClazz);

            sConstruct_0 = env->GetMethodID(sClazz, "<init>", "()V");
            JENNY_CHECK_NULL(sConstruct_0);


            sMethod_getAndRet_0 = env->GetMethodID(sClazz, "getAndRet", "(Ljava/lang/Runnable;)Ljava/lang/Runnable;");
            JENNY_CHECK_NULL(sMethod_getAndRet_0);

            sMethod_genericParam_0 = env->GetMethodID(sClazz, "genericParam", "(Ljava/lang/Object;)V");
            JENNY_CHECK_NULL(sMethod_genericParam_0);

            sMethod_genericParam_1 = env->GetMethodID(sClazz, "genericParam", "(Lio/github/landerlyoung/jennysampleapp/Generic;)V");
            JENNY_CHECK_NULL(sMethod_genericParam_1);

            sMethod_genericParamMultiUpperBounds_0 = env->GetMethodID(sClazz, "genericParamMultiUpperBounds", "(Ljava/lang/Runnable;)V");
            JENNY_CHECK_NULL(sMethod_genericParamMultiUpperBounds_0);

            sMethod_genericParam2_0 = env->GetMethodID(sClazz, "genericParam2", "(Lio/github/landerlyoung/jennysampleapp/Generic;)V");
            JENNY_CHECK_NULL(sMethod_genericParam2_0);

            sMethod_genericParam3_0 = env->GetMethodID(sClazz, "genericParam3", "(Lio/github/landerlyoung/jennysampleapp/Generic;)V");
            JENNY_CHECK_NULL(sMethod_genericParam3_0);

            sMethod_genericParam4_0 = env->GetMethodID(sClazz, "genericParam4", "(Ljava/util/Collection;)V");
            JENNY_CHECK_NULL(sMethod_genericParam4_0);

            sMethod_array_0 = env->GetMethodID(sClazz, "array", "([I)V");
            JENNY_CHECK_NULL(sMethod_array_0);

            sMethod_array_1 = env->GetMethodID(sClazz, "array", "([[I)V");
            JENNY_CHECK_NULL(sMethod_array_1);



            sInited = true;
        }
    }
#undef JENNY_CHECK_NULL
   return true;
}

/*static*/ void GenericProxy::releaseClazz(JNIEnv *env) {
    if (sInited) {
        std::lock_guard<std::mutex> lg(sInitLock);
        if (sInited) {
            env->DeleteGlobalRef(sClazz);
            sClazz = nullptr;
            sInited = false;
        }
    }
}

jmethodID GenericProxy::sConstruct_0;

jmethodID GenericProxy::sMethod_getAndRet_0;
jmethodID GenericProxy::sMethod_genericParam_0;
jmethodID GenericProxy::sMethod_genericParam_1;
jmethodID GenericProxy::sMethod_genericParamMultiUpperBounds_0;
jmethodID GenericProxy::sMethod_genericParam2_0;
jmethodID GenericProxy::sMethod_genericParam3_0;
jmethodID GenericProxy::sMethod_genericParam4_0;
jmethodID GenericProxy::sMethod_array_0;
jmethodID GenericProxy::sMethod_array_1;



