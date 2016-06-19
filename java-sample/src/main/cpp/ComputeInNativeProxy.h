#pragma once

#include <jni.h>
#include <assert.h>

#ifdef __EXCEPTIONS
#include <stdexcept>
#endif

#define CHECK_NULL(val) do {if ((val) == nullptr) return false;} while(false)

class ComputeInNativeProxy {
public:
    static constexpr const char *const FULL_CLASS_NAME = "com/young/jenny/ComputeInNative";


private:
    static jclass sClazz;



    static jfieldID sField_nativeContext_0;

    const bool mGlobal;
    jobject mJavaObjectReference;

public:
    static bool init_clazz(JNIEnv *env) {
        if (sClazz == nullptr) {
            auto localClazz = env->FindClass(FULL_CLASS_NAME);
            CHECK_NULL(localClazz);
            sClazz = reinterpret_cast<jclass>(env->NewGlobalRef(localClazz));
            CHECK_NULL(sClazz);



            sField_nativeContext_0 = env->GetFieldID(sClazz, "nativeContext", "J");
            CHECK_NULL(sField_nativeContext_0);

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



    ///throw std::runtime_error when construct GlobalRef failed
    ComputeInNativeProxy(JNIEnv *env, jobject javaObj, bool global)
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
    ComputeInNativeProxy(const ComputeInNativeProxy &from) = delete;

    void deleteGlobalReference(JNIEnv *env) {
        if (mGlobal) {
            env->DeleteGlobalRef(mJavaObjectReference);
            mJavaObjectReference = nullptr;
        }
    }

    ~ComputeInNativeProxy() {
        assert(!mGlobal || mJavaObjectReference == nullptr);
    }



    jlong getNativeContext(JNIEnv *env) const {
        init_clazz(env);
        return env->GetLongField(mJavaObjectReference, sField_nativeContext_0);
    }

    void setNativeContext(JNIEnv *env, jlong nativeContext) const {
        init_clazz(env);
        env->SetLongField(mJavaObjectReference, sField_nativeContext_0, nativeContext);
    }





};

//static fields
jclass ComputeInNativeProxy::sClazz = nullptr;
jmethodID ComputeInNativeProxy::sConstruct_0 = nullptr;
jmethodID ComputeInNativeProxy::sMethod_init_0 = nullptr;
jmethodID ComputeInNativeProxy::sMethod_request_1 = nullptr;
jmethodID ComputeInNativeProxy::sMethod_release_2 = nullptr;
jmethodID ComputeInNativeProxy::sMethod_getGlobalParam_3 = nullptr;
jmethodID ComputeInNativeProxy::sMethod_setParam_4 = nullptr;
jfieldID ComputeInNativeProxy::sField_nativeContext_0 = nullptr;


#undef CHECK_NULL
