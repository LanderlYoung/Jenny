#include "NestedNativeClass.h"

#ifdef DEBUG
#include <android/log.h>
#define LOGV(...)   __android_log_print((int)ANDROID_LOG_VERBOSE, LOG_TAG, __VA_ARGS__)
#define LOGD(...)   __android_log_print((int)ANDROID_LOG_DEBUG, LOG_TAG, __VA_ARGS__)
#define LOGI(...)   __android_log_print((int)ANDROID_LOG_INFO, LOG_TAG, __VA_ARGS__)
#define LOGW(...)   __android_log_print((int)ANDROID_LOG_WARN, LOG_TAG, __VA_ARGS__)
#define LOGE(...)   __android_log_print((int)ANDROID_LOG_ERROR, LOG_TAG, __VA_ARGS__)
#else
#define LOGV(...)
#define LOGD(...)
#define LOGI(...)
#define LOGW(...)
#define LOGE(...)
#endif


//java class name: com.young.jennysampleapp.ComputeIntensiveClass$NestedNativeClass
static const char* FULL_CLASS_NAME = "com/young/jennysampleapp/ComputeIntensiveClass$NestedNativeClass";
#define constants(cons) com_young_jennysampleapp_ComputeIntensiveClass_00024NestedNativeClass_ ## cons

//change to whatever you like
#define LOG_TAG "NestedNativeClass"

/*
 * Class:     com_young_jennysampleapp_ComputeIntensiveClass_00024NestedNativeClass
 * Method:    public java.util.HashMap<java.lang.String,java.lang.String> one(java.lang.String param)
 * Signature: (Ljava/lang/String;)Ljava/util/HashMap;
 */
JNIEXPORT jobject Java_com_young_jennysampleapp_ComputeIntensiveClass_00024NestedNativeClass_one(JNIEnv *env, jobject thiz, jstring param) {
    return nullptr;
}


/*
 * Class:     com_young_jennysampleapp_ComputeIntensiveClass_00024NestedNativeClass
 * Method:    public long nativeInit()
 * Signature: ()J
 */
JNIEXPORT jlong Java_com_young_jennysampleapp_ComputeIntensiveClass_00024NestedNativeClass_nativeInit(JNIEnv *env, jobject thiz) {
    return 0;
}


/*
 * Class:     com_young_jennysampleapp_ComputeIntensiveClass_00024NestedNativeClass
 * Method:    public void nativeRelease(long handle)
 * Signature: (J)V
 */
JNIEXPORT void Java_com_young_jennysampleapp_ComputeIntensiveClass_00024NestedNativeClass_nativeRelease(JNIEnv *env, jobject thiz, jlong handle) {
    return;
}







