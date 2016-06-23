cmake_minimum_required(VERSION 3.4.1)

add_library(hello-jenny SHARED
    ComputeIntensiveClass.cpp
    NestedNativeClass.cpp
    )

target_link_libraries(hello-jenny log)