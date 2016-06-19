#include "${header}"

${android_log_marcos}
//java class name: ${full_java_class_name}
static const char* FULL_CLASS_NAME = "${full_slash_class_name}";
#define constants(cons) ${full_native_class_name}_ ## cons

//change to whatever you like
#define LOG_TAG "${simple_class_name}"

${methods}

static const JNINativeMethod gsNativeMethods[] = {
${jni_method_struct}
};
static const int gsMethodCount =
    sizeof(gsNativeMethods) / sizeof(JNINativeMethod);

/*
 * registe Native functions
 */
void register_${full_native_class_name}(JNIEnv *env) {
    jclass clazz = env->FindClass(FULL_CLASS_NAME);
    env->RegisterNatives(clazz, gsNativeMethods,gsMethodCount);
}

JNIEXPORT jint JNICALL JNI_OnLoad(JavaVM *vm, void *reserved) {
    JNIEnv* env;
    if (vm->GetEnv(reinterpret_cast<void**>(&env),
                JNI_VERSION_1_6) != JNI_OK) {
        return -1;
    }
    register_${full_native_class_name}(env);
    return JNI_VERSION_1_6;
}

JNIEXPORT void JNICALL JNI_OnUnload(JavaVM *vm, void *reserved) {

}
