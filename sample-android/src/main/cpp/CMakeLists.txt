cmake_minimum_required(VERSION 3.4.1)

include_directories(gen)

file(GLOB SRC
            ./*.cpp
            ./*.h
             )

add_library(hello-jenny SHARED
            ${SRC}
           )

target_link_libraries(hello-jenny log)