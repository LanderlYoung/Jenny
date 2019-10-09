cmake_minimum_required(VERSION 3.4.1)

include_directories(gen)

file(GLOB SRC
            ./*.cpp
            ./*.h
             )
file(GLOB GEN
        gen/*.cpp
        gen/*.h
        )


add_library(hello-jenny SHARED
            ${SRC}
            ${GEN}
           )

target_link_libraries(hello-jenny log)