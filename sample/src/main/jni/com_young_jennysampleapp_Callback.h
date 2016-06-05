#pragma once

#include <jni.h>
#include <assert.h>

#ifdef __EXCEPTIONS
#include <stdexcept>
#endif

#define CHECK_NULL(val) do {if ((val) == nullptr) return false;} while(false)

class com_young_jennysampleapp_Callback {
public:
    static constexpr const char *const FULL_CLASS_NAME = "com/young/jennysampleapp/Callback";
private:
    static jclass sClazz;

    static jmethodID sConstruct_0;
    static jmethodID sConstruct_1;
    static jmethodID sConstruct_2;

    static jmethodID sMethod_prepareRun_0;
    static jmethodID sMethod_onJobStart_1;
    static jmethodID sMethod_onJobDone_2;
    static jmethodID sMethod_onJobProgress_3;
    static jmethodID sMethod_aStaticMethod_4;

    static jfieldID sField_count_0;
    static jfieldID sField_aStaticField_1;
    static jfieldID sField_lock_2;

    const bool mGlobal;
    jobject mJavaObjectReference;

public:
    static bool init_clazz(JNIEnv *env) {
        if (sClazz == nullptr) {
            auto localClazz = env->FindClass(FULL_CLASS_NAME);
            CHECK_NULL(localClazz);
            sClazz = reinterpret_cast<jclass>(env->NewGlobalRef(localClazz));
            CHECK_NULL(sClazz);
                        sConstruct_0 = env->GetMethodID(sClazz, "<init>", "()V");
            CHECK_NULL(sConstruct_0);

            sConstruct_1 = env->GetMethodID(sClazz, "<init>", "(I)V");
            CHECK_NULL(sConstruct_1);

            sConstruct_2 = env->GetMethodID(sClazz, "<init>", "(Ljava/util/HashMap;)V");
            CHECK_NULL(sConstruct_2);

;
                        sMethod_prepareRun_0 = env->GetMethodID(sClazz, "prepareRun", "()I");
            CHECK_NULL(sMethod_prepareRun_0);

            sMethod_onJobStart_1 = env->GetMethodID(sClazz, "onJobStart", "()V");
            CHECK_NULL(sMethod_onJobStart_1);

            sMethod_onJobDone_2 = env->GetMethodID(sClazz, "onJobDone", "(ZLjava/lang/String;)V");
            CHECK_NULL(sMethod_onJobDone_2);

            sMethod_onJobProgress_3 = env->GetMethodID(sClazz, "onJobProgress", "(J)V");
            CHECK_NULL(sMethod_onJobProgress_3);

            sMethod_aStaticMethod_4 = env->GetStaticMethodID(sClazz, "aStaticMethod", "()I");
            CHECK_NULL(sMethod_aStaticMethod_4);

;
                        sField_count_0 = env->GetFieldID(sClazz, "count", "I");
            CHECK_NULL(sField_count_0);            sField_aStaticField_1 = env->GetStaticFieldID(sClazz, "aStaticField", "Ljava/util/ArrayList;");
            CHECK_NULL(sField_aStaticField_1);            sField_lock_2 = env->GetFieldID(sClazz, "lock", "Ljava/lang/Object;");
            CHECK_NULL(sField_lock_2);;
            return true;
        }
        return true;
    }

    static void release_clazz(JNIEnv *env) {
        if (sClazz != nullptr) {
            env->DeleteGlobalRef(sClazz);
            sClazz = nullptr;
        }
    }

    //construct
    static jobject newInstance(JNIEnv *env) noexcept {
        if (init_clazz(env)) {
            return env->NewObject(sClazz, sConstruct_0);
        }
        return nullptr;
    }    //construct
    static jobject newInstance(JNIEnv *env, jint a) noexcept {
        if (init_clazz(env)) {
            return env->NewObject(sClazz, sConstruct_1, a);
        }
        return nullptr;
    }    //construct
    static jobject newInstance(JNIEnv *env, jobject sth) noexcept {
        if (init_clazz(env)) {
            return env->NewObject(sClazz, sConstruct_2, sth);
        }
        return nullptr;
    }

    ///throw std::runtime_error when construct GlobalRef failed
    com_young_jennysampleapp_Callback(JNIEnv *env, jobject javaObj, bool global)
#ifdef __EXCEPTIONS
    throw(std::runtime_error)
#endif
            : mGlobal(global) {
        if (init_clazz(env)) {
            mJavaObjectReference = global ? env->NewGlobalRef(javaObj) : javaObj;
        }
#ifdef __EXCEPTIONS
        if (mGlobal && mJavaObjectReference == nullptr) {
            throw std::runtime_error("cannot create global reference");
        }
#endif
    }

    bool isGlobalJavaReferencePresent() {
        return mJavaObjectReference != nullptr;
    }

    ///no copy construct
    com_young_jennysampleapp_Callback(const com_young_jennysampleapp_Callback &from) = delete;

    void deleteGlobalReference(JNIEnv *env) {
        if (mGlobal) {
            env->DeleteGlobalRef(mJavaObjectReference);
            mJavaObjectReference = nullptr;
        }
    }

    ~com_young_jennysampleapp_Callback() {
        assert(!mGlobal || mJavaObjectReference == nullptr);
    }

    jint prepareRun(JNIEnv *env) const {
        return env->CallIntMethod(mJavaObjectReference, sMethod_prepareRun_0);
    }
    void onJobStart(JNIEnv *env) const {
        env->CallVoidMethod(mJavaObjectReference, sMethod_onJobStart_1);
    }
    void onJobDone(JNIEnv *env, jboolean success, jstring result) const {
        env->CallVoidMethod(mJavaObjectReference, sMethod_onJobDone_2, success, result);
    }
    void onJobProgress(JNIEnv *env, jlong progress) const {
        env->CallVoidMethod(mJavaObjectReference, sMethod_onJobProgress_3, progress);
    }
    static jint aStaticMethod(JNIEnv *env) {
        init_clazz(env);
        return env->CallStaticIntMethod(sClazz, sMethod_aStaticMethod_4);
    }


    jint getCount(JNIEnv *env) const {
        return env->GetIntField(mJavaObjectReference, sField_count_0);
    }

    void setCount(JNIEnv *env, jint count) const {
        env->SetIntField(mJavaObjectReference, sField_count_0, count);
    }    static jobject getAStaticField(JNIEnv *env) {
        init_clazz(env);
        return env->GetStaticObjectField(sClazz, sField_aStaticField_1);
    }

    static void setAStaticField(JNIEnv *env, jobject aStaticField) {
        init_clazz(env);
        env->SetStaticObjectField(sClazz, sField_aStaticField_1, aStaticField);
    }    jobject getLock(JNIEnv *env) const {
        return env->GetObjectField(mJavaObjectReference, sField_lock_2);
    }

    void setLock(JNIEnv *env, jobject lock) const {
        env->SetObjectField(mJavaObjectReference, sField_lock_2, lock);
    }
};

//static fields
jclass com_young_jennysampleapp_Callback::sClazz = nullptr;
jmethodID com_young_jennysampleapp_Callback::sConstruct_0 = nullptr;
jmethodID com_young_jennysampleapp_Callback::sConstruct_1 = nullptr;
jmethodID com_young_jennysampleapp_Callback::sConstruct_2 = nullptr;
jmethodID com_young_jennysampleapp_Callback::sMethod_prepareRun_0 = nullptr;
jmethodID com_young_jennysampleapp_Callback::sMethod_onJobStart_1 = nullptr;
jmethodID com_young_jennysampleapp_Callback::sMethod_onJobDone_2 = nullptr;
jmethodID com_young_jennysampleapp_Callback::sMethod_onJobProgress_3 = nullptr;
jmethodID com_young_jennysampleapp_Callback::sMethod_aStaticMethod_4 = nullptr;
jfieldID com_young_jennysampleapp_Callback::sField_count_0 = nullptr;
jfieldID com_young_jennysampleapp_Callback::sField_aStaticField_1 = nullptr;
jfieldID com_young_jennysampleapp_Callback::sField_lock_2 = nullptr;


#undef CHECK_NULL