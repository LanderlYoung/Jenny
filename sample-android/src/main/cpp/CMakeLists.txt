cmake_minimum_required(VERSION 3.4.1)

set(CMAKE_CXX_STANDARD 20)
set(CMAKE_EXPORT_COMPILE_COMMANDS ON)
project(hello-jenny)

file(GLOB SRC ./*.cpp ./*.h)
add_library(hello-jenny SHARED ${SRC})
target_compile_definitions(hello-jenny PRIVATE JENNY_JNI_HELPER_TEST)
target_include_directories(hello-jenny PRIVATE gen)

target_link_libraries(hello-jenny log)