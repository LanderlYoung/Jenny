//
// Created by landerlyoung on 6/2/16.
//

#pragma once
#ifndef JNI_JAVACALLBACKREFLECT_H
#define JNI_JAVACALLBACKREFLECT_H

#include <jni.h>
#include <assert.h>

#ifdef __USE_CPP_EXCEPTION__
#include <stdexcept>
#endif

#define CHECK_NULL(val) do {if ((val) == nullptr) return false;} while(false)

class JavaCallbackReflect {
    //constants
public:
    static constexpr const char *const CLASS_FULL_NAME = "com/young/jennysampleapp/Callback";
private:
    static jclass sClazz;
    static jmethodID sConstructor_0;
    static jmethodID sMethod_void_onJobDone_1;
    static jmethodID sMethod_void_onJobProgress_2;
    static jmethodID sMethod_void_onJobStart_3;
    static jmethodID sMethod_static_int_aStaticMethod_4;

    bool mGlobal;
    jobject mJavaObjectReference;

public:
    static bool init_clazz(JNIEnv *env) {
        if (sClazz == 0) {
            auto localClazz = env->FindClass(CLASS_FULL_NAME);
            CHECK_NULL(localClazz);
            sClazz = reinterpret_cast<jclass>(env->NewGlobalRef(localClazz));
            CHECK_NULL(sClazz);
//            sConstructor_0 = env->GetMethodID(sClazz, "<init>", "()V");
//            CHECK_NULL(sConstructor_0);
            sMethod_void_onJobDone_1 = env->GetMethodID(sClazz, "onJobDone", "(ZLjava/lang/String;)V");
            CHECK_NULL(sMethod_void_onJobDone_1);
            sMethod_void_onJobProgress_2 = env->GetMethodID(sClazz, "onJobProgress", "(J)V");
            CHECK_NULL(sMethod_void_onJobProgress_2);
            sMethod_void_onJobStart_3 = env->GetMethodID(sClazz, "onJobStart", "()V");
            CHECK_NULL(sMethod_void_onJobStart_3);
            return true;
        }
        return true;
    }

    static void release_clazz(JNIEnv *env) {
        if (sClazz != 0) {
            env->DeleteGlobalRef(sClazz);
            sClazz = 0;
        }
    }

    //construct
    static jobject newInstance(JNIEnv *env) noexcept {
        if (init_clazz(env)) {
        }
        return 0;
    }

    ///throw std::runtime_error when construct GlobalRef failed
    JavaCallbackReflect(JNIEnv *env, jobject javaObj, bool global)
#ifdef __USE_CPP_EXCEPTION__
    throw(std::runtime_error)
#endif
            : mGlobal(global),
              mJavaObjectReference(global ? env->NewGlobalRef(javaObj) : javaObj) {
#ifdef __USE_CPP_EXCEPTION__
        if (mGlobal && mJavaObjectReference == 0) {
            throw std::runtime_error("Out of memory");
        }
#endif
    }

    bool isGlobalJavaReferencePresent() {
        return mJavaObjectReference != 0;
    }

    ///no copy construct
    JavaCallbackReflect(const JavaCallbackReflect &from) = delete;

    void deleteGlobalReference(JNIEnv *env) {
        if (mGlobal) {
            env->DeleteGlobalRef(mJavaObjectReference);
            mJavaObjectReference = 0;
        }
    }

    ~JavaCallbackReflect() {
        assert(!mGlobal || mJavaObjectReference == 0);
    }

    void onJobDone(JNIEnv *env, jboolean success, jstring result) {
        env->CallVoidMethod(mJavaObjectReference, sMethod_void_onJobDone_1, success, result);
    }

    void onJobProgress(JNIEnv *env, jlong progress) {
        env->CallVoidMethod(mJavaObjectReference, sMethod_void_onJobProgress_2, progress);
    }

    void onJobStart(JNIEnv *env) {
        env->CallVoidMethod(mJavaObjectReference, sMethod_void_onJobStart_3);
    }
};

//static fields
jclass JavaCallbackReflect::sClazz = 0;
jmethodID JavaCallbackReflect::sConstructor_0 = 0;
jmethodID JavaCallbackReflect::sMethod_void_onJobDone_1 = 0;
jmethodID JavaCallbackReflect::sMethod_void_onJobProgress_2 = 0;
jmethodID JavaCallbackReflect::sMethod_void_onJobStart_3 = 0;
jmethodID JavaCallbackReflect::sMethod_static_int_aStaticMethod_4 = 0;

#undef __USE_EXCEPTION__
#undef CHECK_NULL
#endif //JNI_JAVACALLBACKREFLECT_H
