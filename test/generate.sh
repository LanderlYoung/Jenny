#!/bin/bash
if [[ -e out ]]; then
    rm -rf out/*
else
    mkdir out
fi

javac -classpath Jenny.jar -d out/ -processor "com.young.util.jni.generator.JNICppSourceGenerateProcessor"  com/young/test/NativeTest.java
