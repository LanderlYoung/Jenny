LOCAL_PATH := $(call my-dir)
include $(CLEAR_VARS)

LOCAL_MODULE := hello-jenny
LOCAL_CFLAGS += -fvisibility=hidden
LOCAL_CPPFLAGS += -O0 -ggdb3 -std=c++11
LOCAL_LDFLAGS += -Wl,--build-id
LOCAL_LDLIBS    += -llog
LOCAL_SRC_FILES += com_young_jennysampleapp_ComputeIntensiveClass.cpp

include $(BUILD_SHARED_LIBRARY)
