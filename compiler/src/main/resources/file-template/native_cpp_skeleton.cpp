#include "${header}"

${android_log_marcos}
//java class name: ${full_java_class_name}
static const char *FULL_CLASS_NAME = "${full_slash_class_name}";
#define constants(cons) ${full_native_class_name}_ ## cons

//change to whatever you like
#define LOG_TAG "${simple_class_name}"

${methods}

${dynamic_jni_register}

${jni_onload_impl}
