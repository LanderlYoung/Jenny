cmake_minimum_required(VERSION 3.4.1)

add_library(hello-jenny SHARED
    com_young_jennysampleapp_ComputeIntensiveClass.cpp
    com_young_jennysampleapp_ComputeIntensiveClass.h JavaCallbackReflect.h)

target_link_libraries(hello-jenny log)