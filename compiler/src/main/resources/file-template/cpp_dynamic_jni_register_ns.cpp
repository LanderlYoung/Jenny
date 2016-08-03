static const JNINativeMethod gsNativeMethods[] = {
${jni_method_struct}
};
static const int gsMethodCount =
    sizeof(gsNativeMethods) / sizeof(JNINativeMethod);

/*
 * register Native functions
 */
void registerNativeFunctions(JNIEnv *env) {
    jclass clazz = env->FindClass("${slash_class_name}");
    env->RegisterNatives(clazz, gsNativeMethods, gsMethodCount);
}

