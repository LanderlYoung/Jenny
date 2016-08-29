cmake_minimum_required(VERSION 3.4.1)

file(GLOB SRC
             *.cpp
             )

add_library(hello-jenny SHARED
            ${SRC}
           )

target_link_libraries(hello-jenny log)