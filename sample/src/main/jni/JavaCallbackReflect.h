//
// Created by landerlyoung on 6/2/16.
//

#ifndef JNI_JAVACALLBACKREFLECT_H
#define JNI_JAVACALLBACKREFLECT_H

#include <jni.h>
#include <stdexcept>
#include <assert.h>

#define CHECK_NULL(val) do {if ((val) == nullptr) return false;} while(false)

class JavaCallbackReflect {
    //constants
public:
    static constexpr const char *const CLASS_FULL_NAME = "com/young/jennysampleapp/Callback";
private:
    static jclass sClazz;
    static jmethodID sConstructor_default;
    static jmethodID sMethod_onJobDone;
    static jmethodID sMethod_onJobProgress;
    static jmethodID sMethod_onJobStart;

    jobject mGlobalJavaObjectReference;

public:
    static inline bool clazz_init(JNIEnv *env) {
        if (sClazz == 0) {
            auto localClazz = env->FindClass(CLASS_FULL_NAME);
            CHECK_NULL(localClazz);
            sClazz = reinterpret_cast<jclass>(env->NewGlobalRef(localClazz));
            CHECK_NULL(sClazz);
            sConstructor_default = env->GetMethodID(sClazz, "<init>", "()V");
            CHECK_NULL(sConstructor_default);
            sMethod_onJobDone = env->GetMethodID(sClazz, "onJobDone", "(Z[java.lang.String;)V");
            CHECK_NULL(sMethod_onJobDone);
            sMethod_onJobProgress = env->GetMethodID(sClazz, "onJobProgress", "(J)V");
            CHECK_NULL(sMethod_onJobProgress);
            sMethod_onJobStart = env->GetMethodID(sClazz, "onJobStart", "()V");
            CHECK_NULL(sMethod_onJobStart);
            return true;
        }
        return true;
    }

    //construct
    static inline jobject newInstance(JNIEnv *env) noexcept {
        if (clazz_init(env)) {
        }
        return 0;
    }

    ///throw std::runtime_error when construct GlobalRef failed
    JavaCallbackReflect(JNIEnv *env, jobject javaObj) throw(std::runtime_error)
            : mGlobalJavaObjectReference(env->NewGlobalRef(javaObj)) {
        if (mGlobalJavaObjectReference == 0) {
            throw std::runtime_error("Out of memory");
        }
    }

    ///no copy construct
    JavaCallbackReflect(const JavaCallbackReflect &from) = delete;

    void deleteGlobalReference(JNIEnv *env) {
        env->DeleteGlobalRef(mGlobalJavaObjectReference);
        mGlobalJavaObjectReference = 0;
    }

    ~JavaCallbackReflect() {
        assert(mGlobalJavaObjectReference == 0);
    }
};

#undef CHECK_NULL
#endif //JNI_JAVACALLBACKREFLECT_H
