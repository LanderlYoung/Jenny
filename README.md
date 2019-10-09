# Jenny

[![CI][CI_B]][CI]  [![Publish][PUB_B]][PUB] [![Download][BT_B]][BT] ![GitHub code size in bytes][CS_B] ![GitHub][LC_B]

[CI_B]: https://github.com/LanderlYoung/Jenny/workflows/Android%20CI/badge.svg
[CI]: https://github.com/LanderlYoung/Jenny/actions?workflow=Android+CI
[PUB_B]: https://github.com/LanderlYoung/Jenny/workflows/Publish/badge.svg
[PUB]: https://github.com/LanderlYoung/Jenny/actions?workflow=Publish
[BT_B]: https://api.bintray.com/packages/landerlyoung/maven/jenny-annotation/images/download.svg
[BT]: https://bintray.com/landerlyoung/maven
[CS_B]: https://img.shields.io/github/languages/code-size/LanderlYoung/Jenny
[LC_B]: https://img.shields.io/github/license/LanderlYoung/Jenny

**JNI glue code generator**

This is a java annotation processor, which helps you generate C/C++ code for JNI calls according to your java native class.

### Source structure
source code contains two Annotation classes: `NativeClass` and `NativeSource`, and a Annotation processor.

### How to use it

#### First

Add `@NativeClass()`annotation to you native class in order to help Annotation Processor spot you class, and then generate corresponding cpp source.

You can also add `@NativeSource("Cpp code")` to native method, then the generator will fill you cpp function with given code.

sample:

```java
package com.young.test;

//annotate native class
@NativeClass
public class NativeTest {

    //you can fill simple cpp function with some code
    @NativeSource(
    "jint c = a + b;\n" +
    "return c;")
    public native int add(int a, int b);

    public native void cpp_magic(String s, byte[] data);
}
```


#### Second

Process you java code with Java Annotation Processor.

The Processor class is `io.github.landerlyoung.jenny.JennyAnnotationProcessor`. You can pass the processor to your javac command with switch `-processor`, like `javac -classpath Jenny.jar -d out/ -processor "io.github.landerlyoung.jenny.JennyAnnotationProcessor"  com/young/test/NativeTest.java`.

See test for more details.


If you are using IDEs like IntelliJ IDEA or Eclipse, google it to see how to add annotation processors.

### 2.See it's power

By default, Jenny will generate .h file and .cpp file for each class, and each class has it's own `JNI_OnLoad` and `JNI_OnUnload`. In `JNI_OnLoad`, a function named register_<java class name> function will be called to register native functions. So, if you want integrate them into one dynamic library(.dll in windows, .so in linux/unix, .dylib in OSX), just eliminate those two functions in you cpp, and keep one pair of them in one dynamic library, and remember to gerister your native methods.

here is a sample code generated form java class above:

header file com_young_test_NativeTest.h
```cpp
/* 
 * JNI Header file generated by annotation JNI helper
 * written by landerlyoung@gmail.com
 */

/* C/C++ header file for class com.young.test.NativeTest */
#ifndef _Included_com_young_test_NativeTest
#define _Included_com_young_test_NativeTest

#include <jni.h>

/*
 * Class:     com_young_test_NativeTest
 * Method:    com.young.test.NativeTest::add
 * Signature: (II)I
 */
jint add(JNIEnv *env, jobject thiz, jint a, jint b);

/*
 * Class:     com_young_test_NativeTest
 * Method:    com.young.test.NativeTest::cpp_magic
 * Signature: (Ljava/lang/String;Ljava/lang/RuntimeException;)V
 */
void cpp_magic(JNIEnv *env, jobject thiz, jstring s, jthrowable re);

/*
 * registe Native functions
 */
void register_com_young_test_NativeTest(JNIEnv *env);

#ifdef __cplusplus
extern "C" {
#endif
JNIEXPORT jint JNICALL JNI_OnLoad(JavaVM *vm, void *reserved);
JNIEXPORT void JNICALL JNI_OnUnload(JavaVM *vm, void *reserved);
#ifdef __cplusplus
}
#endif

#endif
```

cpp file com_young_test_NativeTest.cpp

```cpp
#include "com_young_test_NativeTest.h"

//java class name: com.young.test.NativeTest
#define FULL_CLASS_NAME "com/young/test/NativeTest"
#define constants(cons) com_young_test_NativeTest_ ## cons

/*
 * Class:     com_young_test_NativeTest
 * Method:    com.young.test.NativeTest::add
 * Signature: (II)I
 */
jint add(JNIEnv *env, jobject thiz, jint a, jint b) {
    jint c = a + b;
return c;
}

/*
 * Class:     com_young_test_NativeTest
 * Method:    com.young.test.NativeTest::cpp_magic
 * Signature: (Ljava/lang/String;Ljava/lang/RuntimeException;)V
 */
void cpp_magic(JNIEnv *env, jobject thiz, jstring s, jthrowable re) {
}

static const JNINativeMethod gsNativeMethods[] = {
    {
        const_cast<char *>("add"),
        const_cast<char *>("(II)I"),
        reinterpret_cast<void *>(add)
    }, {
        const_cast<char *>("cpp_magic"),
        const_cast<char *>("(Ljava/lang/String;Ljava/lang/RuntimeException;)V"),
        reinterpret_cast<void *>(cpp_magic)
    }
};
static const int gsMethodCount =
    sizeof(gsNativeMethods) / sizeof(JNINativeMethod); //2

/*
 * registe Native functions
 */
void register_com_young_test_NativeTest(JNIEnv *env) {
    jclass clazz = env->FindClass(FULL_CLASS_NAME);
    env->RegisterNatives(clazz, gsNativeMethods,gsMethodCount);
}

JNIEXPORT jint JNICALL JNI_OnLoad(JavaVM *vm, void *reserved) {
    JNIEnv* env;
    if (vm->GetEnv(reinterpret_cast<void**>(&env),
                JNI_VERSION_1_6) != JNI_OK) {
        return -1;
    }
    register_com_young_test_NativeTest(env);
    return JNI_VERSION_1_6;
}

JNIEXPORT void JNICALL JNI_OnUnload(JavaVM *vm, void *reserved) {
    
}
```

#### have fun with Jenny .^_^.

