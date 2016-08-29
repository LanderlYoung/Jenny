/**
 * File generated by Jenny -- https://github.com/LanderlYoung/Jenny
 *
 * DO NOT EDIT THIS FILE WITHOUT COPYING TO YOUR SRC DIRECTORY.
 *
 * For bug report, please refer to github issue tracker https://github.com/LanderlYoung/Jenny/issues,
 * or contact author landerlyoung@gmail.com.
 */
#include "${header}"

${android_log_marcos}

//change to whatever you like
static constexpr auto LOG_TAG = "${simple_class_name}";

//DO NOT modify
static constexpr auto FULL_CLASS_NAME = "${full_slash_class_name}";

namespace ${namespace} {

${methods}

${dynamic_jni_register}

} //endof namespace ${namespace}

${jni_onload_impl}
