#pragma once

#include <jni.h>
#include <assert.h>

#ifdef __EXCEPTIONS
#include <stdexcept>
#endif

#define CHECK_NULL(val) do {if ((val) == nullptr) return false;} while(false)

class CallbackProxy {
public:
    static constexpr const char *const FULL_CLASS_NAME = "com/young/jennysampleapp/Callback";

    static constexpr const jint COMPILE_CONSTANT_INT = 15;
    static constexpr const jint ANOTHER_COMPILE_CONSTANT_INT = 16;

private:
    static jclass sClazz;

    static jmethodID sConstruct_0;
    static jmethodID sConstruct_1;
    static jmethodID sConstruct_2;

    static jmethodID sMethod_onJobStart_0;
    static jmethodID sMethod_onJobDone_1;
    static jmethodID sMethod_onJobProgress_2;

    static jfieldID sField_count_0;
    static jfieldID sField_staticName_1;
    static jfieldID sField_aStaticField_2;
    static jfieldID sField_name_3;
    static jfieldID sField_lock_4;
    static jfieldID sField_staticCount_5;

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

            sMethod_onJobStart_0 = env->GetMethodID(sClazz, "onJobStart", "()V");
            CHECK_NULL(sMethod_onJobStart_0);
            sMethod_onJobDone_1 = env->GetMethodID(sClazz, "onJobDone", "(ZLjava/lang/String;)V");
            CHECK_NULL(sMethod_onJobDone_1);
            sMethod_onJobProgress_2 = env->GetMethodID(sClazz, "onJobProgress", "(J)V");
            CHECK_NULL(sMethod_onJobProgress_2);

            sField_count_0 = env->GetFieldID(sClazz, "count", "I");
            CHECK_NULL(sField_count_0);
            sField_staticName_1 = env->GetStaticFieldID(sClazz, "staticName", "Ljava/lang/String;");
            CHECK_NULL(sField_staticName_1);
            sField_aStaticField_2 = env->GetStaticFieldID(sClazz, "aStaticField", "Ljava/util/List;");
            CHECK_NULL(sField_aStaticField_2);
            sField_name_3 = env->GetFieldID(sClazz, "name", "Ljava/lang/String;");
            CHECK_NULL(sField_name_3);
            sField_lock_4 = env->GetFieldID(sClazz, "lock", "Ljava/lang/Object;");
            CHECK_NULL(sField_lock_4);
            sField_staticCount_5 = env->GetStaticFieldID(sClazz, "staticCount", "I");
            CHECK_NULL(sField_staticCount_5);

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
    }

    //construct
    static jobject newInstance(JNIEnv *env, jint a) noexcept {
        if (init_clazz(env)) {
            return env->NewObject(sClazz, sConstruct_1, a);
        }
        return nullptr;
    }

    //construct
    static jobject newInstance(JNIEnv *env, jobject sth) noexcept {
        if (init_clazz(env)) {
            return env->NewObject(sClazz, sConstruct_2, sth);
        }
        return nullptr;
    }



    ///throw std::runtime_error when construct GlobalRef failed
    CallbackProxy(JNIEnv *env, jobject javaObj, bool global)
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

    bool isGlobalReferencePresent() {
        return mJavaObjectReference != nullptr;
    }

    ///no copy construct
    CallbackProxy(const CallbackProxy &from) = delete;

    void deleteGlobalReference(JNIEnv *env) {
        if (mGlobal) {
            env->DeleteGlobalRef(mJavaObjectReference);
            mJavaObjectReference = nullptr;
        }
    }

    ~CallbackProxy() {
        assert(!mGlobal || mJavaObjectReference == nullptr);
    }

    void onJobStart(JNIEnv *env) const {
        env->CallVoidMethod(mJavaObjectReference, sMethod_onJobStart_0);
    }

    void onJobDone(JNIEnv *env, jboolean success, jstring result) const {
        env->CallVoidMethod(mJavaObjectReference, sMethod_onJobDone_1, success, result);
    }

    void onJobProgress(JNIEnv *env, jlong progress) const {
        env->CallVoidMethod(mJavaObjectReference, sMethod_onJobProgress_2, progress);
    }



    jint getCount(JNIEnv *env) const {
        init_clazz(env);
        return env->GetIntField(mJavaObjectReference, sField_count_0);
    }

    void setCount(JNIEnv *env, jint count) const {
        init_clazz(env);
        env->SetIntField(mJavaObjectReference, sField_count_0, count);
    }



    static jstring getStaticName(JNIEnv *env) {
        init_clazz(env);
        return reinterpret_cast<jstring>(env->GetStaticObjectField(sClazz, sField_staticName_1));
    }

    static void setStaticName(JNIEnv *env, jstring staticName) {
        init_clazz(env);
        env->SetStaticObjectField(sClazz, sField_staticName_1, staticName);
    }



    static jobject getAStaticField(JNIEnv *env) {
        init_clazz(env);
        return env->GetStaticObjectField(sClazz, sField_aStaticField_2);
    }

    static void setAStaticField(JNIEnv *env, jobject aStaticField) {
        init_clazz(env);
        env->SetStaticObjectField(sClazz, sField_aStaticField_2, aStaticField);
    }



    jstring getName(JNIEnv *env) const {
        init_clazz(env);
        return reinterpret_cast<jstring>(env->GetObjectField(mJavaObjectReference, sField_name_3));
    }



    jobject getLock(JNIEnv *env) const {
        init_clazz(env);
        return env->GetObjectField(mJavaObjectReference, sField_lock_4);
    }

    void setLock(JNIEnv *env, jobject lock) const {
        init_clazz(env);
        env->SetObjectField(mJavaObjectReference, sField_lock_4, lock);
    }



    static jint getStaticCount(JNIEnv *env) {
        init_clazz(env);
        return env->GetStaticIntField(sClazz, sField_staticCount_5);
    }

    static void setStaticCount(JNIEnv *env, jint staticCount) {
        init_clazz(env);
        env->SetStaticIntField(sClazz, sField_staticCount_5, staticCount);
    }





};

//static fields
jclass CallbackProxy::sClazz = nullptr;
jmethodID CallbackProxy::sConstruct_0 = nullptr;
jmethodID CallbackProxy::sConstruct_1 = nullptr;
jmethodID CallbackProxy::sConstruct_2 = nullptr;
jmethodID CallbackProxy::sMethod_onJobStart_0 = nullptr;
jmethodID CallbackProxy::sMethod_onJobDone_1 = nullptr;
jmethodID CallbackProxy::sMethod_onJobProgress_2 = nullptr;
jfieldID CallbackProxy::sField_count_0 = nullptr;
jfieldID CallbackProxy::sField_staticName_1 = nullptr;
jfieldID CallbackProxy::sField_aStaticField_2 = nullptr;
jfieldID CallbackProxy::sField_name_3 = nullptr;
jfieldID CallbackProxy::sField_lock_4 = nullptr;
jfieldID CallbackProxy::sField_staticCount_5 = nullptr;


#undef CHECK_NULL
