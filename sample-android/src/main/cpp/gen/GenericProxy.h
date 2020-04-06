/**
 * File generated by Jenny -- https://github.com/LanderlYoung/Jenny
 *
 * DO NOT EDIT THIS FILE.
 *
 * For bug report, please refer to github issue tracker https://github.com/LanderlYoung/Jenny/issues.
 */
#pragma once

#include <jni.h>
#include <assert.h>                        
#include <atomic>
#include <mutex>


class GenericProxy {

public:
    static constexpr auto FULL_CLASS_NAME = "io/github/landerlyoung/jennysampleapp/Generic";



private:
    // thread safe init
    static std::atomic_bool sInited;
    static std::mutex sInitLock;

    JNIEnv* mJniEnv;
    jobject mJavaObjectReference;

public:

    static bool initClazz(JNIEnv* env);
    
    static void releaseClazz(JNIEnv* env);

    static void assertInited(JNIEnv* env) {
        auto initClazzSuccess = initClazz(env);
        assert(initClazzSuccess);
    }

    GenericProxy(JNIEnv* env, jobject javaObj)
            : mJniEnv(env), mJavaObjectReference(javaObj) {
        if (env) { assertInited(env); }
    }

    GenericProxy(const GenericProxy& from) = default;
    GenericProxy &operator=(const GenericProxy &) = default;

    GenericProxy(GenericProxy&& from) noexcept
           : mJniEnv(from.mJniEnv), mJavaObjectReference(from.mJavaObjectReference) {
        from.mJavaObjectReference = nullptr;
    }
    
    GenericProxy& operator=(GenericProxy&& from) noexcept {
       mJniEnv = from.mJniEnv;
       std::swap(mJavaObjectReference, from.mJavaObjectReference);
       return *this;
   }

    ~GenericProxy() = default;
    
    // helper method to get underlay jobject reference
    jobject operator*() const {
       return mJavaObjectReference;
    }
    
    // helper method to check underlay jobject reference is not nullptr
    operator bool() const {
       return mJavaObjectReference;
    }
    
    // helper method to delete JNI local ref.
    // use only when you really understand JNIEnv::DeleteLocalRef.
    void deleteLocalRef() {
       if (mJavaObjectReference) {
           mJniEnv->DeleteLocalRef(mJavaObjectReference);
           mJavaObjectReference = nullptr;
       }
    }
    
    // === java methods below ===
    
    // construct: public Generic()
    static GenericProxy newInstance(JNIEnv* env) noexcept {
       assertInited(env);
       return GenericProxy(env, env->NewObject(sClazz, sConstruct_0));
    } 
    

    // method: public final T getAndRet(T t)
    jobject getAndRet(jobject t) const {
        return mJniEnv->CallObjectMethod(mJavaObjectReference, sMethod_getAndRet_0, t);
    }

    // method: public final void genericParam(R r)
    void genericParam__Ljava_lang_Object_2(jobject r) const {
        mJniEnv->CallVoidMethod(mJavaObjectReference, sMethod_genericParam_0, r);
    }

    // method: public final void genericParam(io.github.landerlyoung.jennysampleapp.Generic<java.lang.Runnable> t)
    void genericParam__Lio_github_landerlyoung_jennysampleapp_Generic_2(jobject t) const {
        mJniEnv->CallVoidMethod(mJavaObjectReference, sMethod_genericParam_1, t);
    }

    // method: public final void genericParamMultiUpperBounds(R r)
    void genericParamMultiUpperBounds(jobject r) const {
        mJniEnv->CallVoidMethod(mJavaObjectReference, sMethod_genericParamMultiUpperBounds_0, r);
    }

    // method: public final void genericParam2(io.github.landerlyoung.jennysampleapp.Generic<java.util.concurrent.FutureTask<java.lang.Object>> t)
    void genericParam2(jobject t) const {
        mJniEnv->CallVoidMethod(mJavaObjectReference, sMethod_genericParam2_0, t);
    }

    // method: public final void genericParam3(io.github.landerlyoung.jennysampleapp.Generic<R> t)
    void genericParam3(jobject t) const {
        mJniEnv->CallVoidMethod(mJavaObjectReference, sMethod_genericParam3_0, t);
    }

    // method: public final void genericParam4(java.util.Collection<? extends java.lang.Runnable> t)
    void genericParam4(jobject t) const {
        mJniEnv->CallVoidMethod(mJavaObjectReference, sMethod_genericParam4_0, t);
    }

    // method: public final void array(int[] ia)
    void array(jintArray ia) const {
        mJniEnv->CallVoidMethod(mJavaObjectReference, sMethod_array_0, ia);
    }

    // method: public final void array(int[][] ia)
    void array(jobjectArray ia) const {
        mJniEnv->CallVoidMethod(mJavaObjectReference, sMethod_array_1, ia);
    }



private:
    static jclass sClazz;
    static jmethodID sConstruct_0;

    static jmethodID sMethod_getAndRet_0;
    static jmethodID sMethod_genericParam_0;
    static jmethodID sMethod_genericParam_1;
    static jmethodID sMethod_genericParamMultiUpperBounds_0;
    static jmethodID sMethod_genericParam2_0;
    static jmethodID sMethod_genericParam3_0;
    static jmethodID sMethod_genericParam4_0;
    static jmethodID sMethod_array_0;
    static jmethodID sMethod_array_1;


};

