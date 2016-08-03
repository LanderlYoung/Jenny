#include "${header}"

${android_log_marcos}

//change to whatever you like
#define LOG_TAG "${simple_class_name}"

namespace ${namespace} {

${methods}

${dynamic_jni_register}

} //endof namespace ${namespace}

${jni_onload_impl}
