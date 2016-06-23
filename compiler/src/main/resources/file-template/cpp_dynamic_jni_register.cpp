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

