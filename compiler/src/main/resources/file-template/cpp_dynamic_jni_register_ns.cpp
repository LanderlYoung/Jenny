static const JNINativeMethod gsNativeMethods[] = {
${jni_method_struct}
};
static const int gsMethodCount =
    sizeof(gsNativeMethods) / sizeof(JNINativeMethod);

/**
 * register Native functions
 * @returns success or not
 */
bool registerNativeFunctions(JNIEnv *env) {
    jclass clazz = env->FindClass(FULL_CLASS_NAME);
    return clazz != nullptr
           && 0 == env->RegisterNatives(clazz, gsNativeMethods, gsMethodCount);
}

