#!/bin/bash
rm -rf out/*
javac -classpath Jenny.jar -d out/ -processor "com.young.util.jni.generator.JNICppSourceGenerateProcessor"  com/young/test/NativeTest.java
