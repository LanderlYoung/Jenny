#cmake_minimum_required(VERSION 3.4.1)
#
#project(HelloJenny VERSION 1.0 LANGUAGES CXX)
#set(CMAKE_CXX_STANDARD 11)
#set(CMAKE_CXX_STANDARD_REQUIRED ON)
#
#file(GLOB_RECURSE SRC_CPP
#        ${CMAKE_SOURCE_DIR}/gen/jenny/glue/cpp/*.cpp
#        ${CMAKE_SOURCE_DIR}/gen/jenny/proxy/*.cpp
#        ${CMAKE_SOURCE_DIR}/*.cpp
#)
#file(GLOB_RECURSE SRC_H
#        ${CMAKE_SOURCE_DIR}/gen/jenny/glue/header/*.h
#        ${CMAKE_SOURCE_DIR}/gen/jenny/proxy/*.h
#)
#set(SRC ${SRC_CPP} ${SRC_H})
#add_library(hello-jenny SHARED ${SRC})
#target_include_directories(hello-jenny PRIVATE ${CMAKE_SOURCE_DIR}/gen/jenny)
#target_link_libraries(hello-jenny PRIVATE log)