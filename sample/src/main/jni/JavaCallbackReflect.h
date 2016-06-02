//
// Created by landerlyoung on 6/2/16.
//

#ifndef JNI_JAVACALLBACKREFLECT_H
#define JNI_JAVACALLBACKREFLECT_H

#include <jni.h>

template<>
class JavaCallbackReflect {
private:
    static const char *CLASS_FULL_NAME = "com/young/jennysampleapp/Callback";
    jmethodID mMethod_onJobDone;
    jmethodID mMethos_onJobProgress;

public:
    jobject newInstance() {

    }
};

#endif //JNI_JAVACALLBACKREFLECT_H
