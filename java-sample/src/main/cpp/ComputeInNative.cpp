#include "ComputeInNative.h"



//change to whatever you like
#define LOG_TAG "ComputeInNative"

namespace ComputeInNative {

/*
 * Class:     com_young_jenny_ComputeInNative
 * Method:    public boolean init()
 * Signature: ()Z
 */
jboolean init(JNIEnv *env, jobject thiz) {
    return JNI_FALSE;
}


/*
 * Class:     com_young_jenny_ComputeInNative
 * Method:    public void release()
 * Signature: ()V
 */
void release(JNIEnv *env, jobject thiz) {
    return;
}


/*
 * Class:     com_young_jenny_ComputeInNative
 * Method:    public void setParam(java.util.Map<java.lang.String,java.lang.String> globalHttpParam)
 * Signature: (Ljava/util/Map;)V
 */
void setParam(JNIEnv *env, jobject thiz, jobject globalHttpParam) {
    return;
}


/*
 * Class:     com_young_jenny_ComputeInNative
 * Method:    public java.util.Map<java.lang.String,java.lang.String> getGlobalParam()
 * Signature: ()Ljava/util/Map;
 */
jobject getGlobalParam(JNIEnv *env, jobject thiz) {
    return nullptr;
}


/*
 * Class:     com_young_jenny_ComputeInNative
 * Method:    public boolean request(java.lang.String json, com.young.jenny.RequestListener listener)
 * Signature: (Ljava/lang/String;Lcom/young/jenny/RequestListener;)Z
 */
jboolean request(JNIEnv *env, jobject thiz, jstring json, jobject listener) {
    return JNI_FALSE;
}




static const JNINativeMethod gsNativeMethods[] = {
    {
        /* method name      */ const_cast<char *>("init"),
        /* method signature */ const_cast<char *>("()Z"),
        /* function pointer */ reinterpret_cast<void *>(init)
    },    {
        /* method name      */ const_cast<char *>("release"),
        /* method signature */ const_cast<char *>("()V"),
        /* function pointer */ reinterpret_cast<void *>(release)
    },    {
        /* method name      */ const_cast<char *>("setParam"),
        /* method signature */ const_cast<char *>("(Ljava/util/Map;)V"),
        /* function pointer */ reinterpret_cast<void *>(setParam)
    },    {
        /* method name      */ const_cast<char *>("getGlobalParam"),
        /* method signature */ const_cast<char *>("()Ljava/util/Map;"),
        /* function pointer */ reinterpret_cast<void *>(getGlobalParam)
    },    {
        /* method name      */ const_cast<char *>("request"),
        /* method signature */ const_cast<char *>("(Ljava/lang/String;Lcom/young/jenny/RequestListener;)Z"),
        /* function pointer */ reinterpret_cast<void *>(request)
    }
};
static const int gsMethodCount =
    sizeof(gsNativeMethods) / sizeof(JNINativeMethod);

/*
 * register Native functions
 */
void registerNativeFunctions(JNIEnv *env) {
    jclass clazz = env->FindClass("com/young/jenny/ComputeInNative");
    env->RegisterNatives(clazz, gsNativeMethods, gsMethodCount);
}



} //endof namespace ComputeInNative


JNIEXPORT jint JNICALL JNI_OnLoad(JavaVM *vm, void *reserved) {
    JNIEnv *env;
    if (vm->GetEnv(reinterpret_cast<void**>(&env),
                   JNI_VERSION_1_6) != JNI_OK) {
        return -1;
    }
    ComputeInNative::registerNativeFunctions(env);
    return JNI_VERSION_1_6;
}

JNIEXPORT void JNICALL JNI_OnUnload(JavaVM *vm, void *reserved) {

}
