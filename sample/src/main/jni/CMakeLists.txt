cmake_minimum_required(VERSION 3.5)
project(jni)

include_directories(
        /Users/young/Applications/android/android-ndk-r10e/platforms/android-21/arch-arm64/usr/include/
)
set(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} -std=c++11")

set(SOURCE_FILES
    com_young_jennysampleapp_ComputeIntensiveClass.cpp
    com_young_jennysampleapp_ComputeIntensiveClass.h JavaCallbackReflect.h)

add_library(jni ${SOURCE_FILES})
