#pragma once

#include <jni.h>
#include <assert.h>

#ifdef __EXCEPTIONS
#include <stdexcept>
#endif

#define CHECK_NULL(val) do {if ((val) == nullptr) return false;} while(false)

class com_young_jennysampleapp_Callback_NestedClass {
public:
    static constexpr const char *const FULL_CLASS_NAME = "com/young/jennysampleapp/Callback$NestedClass";
private:
    static jclass sClazz;

    static jmethodID sConstruct_0;



    const bool mGlobal;
    jobject mJavaObjectReference;

public:
    static bool init_clazz(JNIEnv *env) {
        if (sClazz == nullptr) {
            auto localClazz = env->FindClass(FULL_CLASS_NAME);
            CHECK_NULL(localClazz);
            sClazz = reinterpret_cast<jclass>(env->NewGlobalRef(localClazz));
            CHECK_NULL(sClazz);

            sConstruct_0 = env->GetMethodID(sClazz, "<init>", "(Lcom/young/jennysampleapp/Callback;)V");
            CHECK_NULL(sConstruct_0);



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
    static jobject newInstance(JNIEnv *env, jobject enclosingClass) noexcept {
        if (init_clazz(env)) {
            return env->NewObject(sClazz, sConstruct_0, enclosingClass);
        }
        return nullptr;
    }



    ///throw std::runtime_error when construct GlobalRef failed
    com_young_jennysampleapp_Callback_NestedClass(JNIEnv *env, jobject javaObj, bool global)
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
    com_young_jennysampleapp_Callback_NestedClass(const com_young_jennysampleapp_Callback_NestedClass &from) = delete;

    void deleteGlobalReference(JNIEnv *env) {
        if (mGlobal) {
            env->DeleteGlobalRef(mJavaObjectReference);
            mJavaObjectReference = nullptr;
        }
    }

    ~com_young_jennysampleapp_Callback_NestedClass() {
        assert(!mGlobal || mJavaObjectReference == nullptr);
    }





};

//static fields
jclass com_young_jennysampleapp_Callback_NestedClass::sClazz = nullptr;
jmethodID com_young_jennysampleapp_Callback_NestedClass::sConstruct_0 = nullptr;


#undef CHECK_NULL