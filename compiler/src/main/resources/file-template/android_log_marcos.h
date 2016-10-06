#include <android/log.h>

#define LOGI(...)   __android_log_print((int)ANDROID_LOG_INFO, LOG_TAG, __VA_ARGS__)
#define LOGW(...)   __android_log_print((int)ANDROID_LOG_WARN, LOG_TAG, __VA_ARGS__)
#define LOGE(...)   __android_log_print((int)ANDROID_LOG_ERROR, LOG_TAG, __VA_ARGS__)

#ifdef DEBUG
//LOGV LOGD only displays on debug build
#define LOGV(...)   __android_log_print((int)ANDROID_LOG_VERBOSE, LOG_TAG, __VA_ARGS__)
#define LOGD(...)   __android_log_print((int)ANDROID_LOG_DEBUG, LOG_TAG, __VA_ARGS__)
#else
#define LOGV(...)
#define LOGD(...)
#endif

