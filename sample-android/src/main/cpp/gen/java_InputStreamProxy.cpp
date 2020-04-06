/**
 * File generated by Jenny -- https://github.com/LanderlYoung/Jenny
 *
 * DO NOT EDIT THIS FILE.
 *
 * For bug report, please refer to github issue tracker https://github.com/LanderlYoung/Jenny/issues.
 */
#include "java_InputStreamProxy.h"


// external logger function passed by jenny.errorLoggerFunction
void jennySampleErrorLog(JNIEnv* env, const char* error);


namespace java {

jclass InputStreamProxy::sClazz = nullptr;

// thread safe init
std::mutex InputStreamProxy::sInitLock;
std::atomic_bool InputStreamProxy::sInited;

/*static*/ bool InputStreamProxy::initClazz(JNIEnv* env) {
#define JENNY_CHECK_NULL(val)                      \
       do {                                        \
           if ((val) == nullptr) {                 \
               jennySampleErrorLog(env, "can't init InputStreamProxy::" #val); \
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


            sMethod_read_0 = env->GetMethodID(sClazz, "read", "()I");
            JENNY_CHECK_NULL(sMethod_read_0);

            sMethod_read_1 = env->GetMethodID(sClazz, "read", "([B)I");
            JENNY_CHECK_NULL(sMethod_read_1);

            sMethod_read_2 = env->GetMethodID(sClazz, "read", "([BII)I");
            JENNY_CHECK_NULL(sMethod_read_2);

            sMethod_skip_0 = env->GetMethodID(sClazz, "skip", "(J)J");
            JENNY_CHECK_NULL(sMethod_skip_0);

            sMethod_available_0 = env->GetMethodID(sClazz, "available", "()I");
            JENNY_CHECK_NULL(sMethod_available_0);

            sMethod_close_0 = env->GetMethodID(sClazz, "close", "()V");
            JENNY_CHECK_NULL(sMethod_close_0);

            sMethod_mark_0 = env->GetMethodID(sClazz, "mark", "(I)V");
            JENNY_CHECK_NULL(sMethod_mark_0);

            sMethod_reset_0 = env->GetMethodID(sClazz, "reset", "()V");
            JENNY_CHECK_NULL(sMethod_reset_0);

            sMethod_markSupported_0 = env->GetMethodID(sClazz, "markSupported", "()Z");
            JENNY_CHECK_NULL(sMethod_markSupported_0);



            sInited = true;
        }
    }
#undef JENNY_CHECK_NULL
   return true;
}

/*static*/ void InputStreamProxy::releaseClazz(JNIEnv* env) {
    if (sInited) {
        std::lock_guard<std::mutex> lg(sInitLock);
        if (sInited) {
            env->DeleteGlobalRef(sClazz);
            sClazz = nullptr;
            sInited = false;
        }
    }
}

jmethodID InputStreamProxy::sConstruct_0;

jmethodID InputStreamProxy::sMethod_read_0;
jmethodID InputStreamProxy::sMethod_read_1;
jmethodID InputStreamProxy::sMethod_read_2;
jmethodID InputStreamProxy::sMethod_skip_0;
jmethodID InputStreamProxy::sMethod_available_0;
jmethodID InputStreamProxy::sMethod_close_0;
jmethodID InputStreamProxy::sMethod_mark_0;
jmethodID InputStreamProxy::sMethod_reset_0;
jmethodID InputStreamProxy::sMethod_markSupported_0;


} // endof namespace java
