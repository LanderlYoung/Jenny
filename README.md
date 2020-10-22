# Jenny -- the JNI helper

[![CI][CI_B]][CI]  [![Publish][PUB_B]][PUB] [![Download][BT_B]][BT] ![GitHub code size in bytes][CS_B] ![GitHub][LC_B]

[CI_B]: https://github.com/LanderlYoung/Jenny/workflows/Android%20CI/badge.svg
[CI]: https://github.com/LanderlYoung/Jenny/actions?workflow=Android+CI
[PUB_B]: https://github.com/LanderlYoung/Jenny/workflows/Publish/badge.svg
[PUB]: https://github.com/LanderlYoung/Jenny/actions?workflow=Publish
[BT_B]: https://api.bintray.com/packages/landerlyoung/maven/jenny-annotation/images/download.svg
[BT]: https://bintray.com/landerlyoung/maven
[CS_B]: https://img.shields.io/github/languages/code-size/LanderlYoung/Jenny
[LC_B]: https://img.shields.io/github/license/LanderlYoung/Jenny

---


## Intro

**Jenny is a java annotation processor, which helps you generate C/C++ code for JNI calls according to your java native class.**

Jenny comes with two main part:
1. Native**Glue**Generator: which generate skeleton C++ code for your native class/method.
2. Native**Proxy**Generator: which generate helper C++ class for you to call java APIs through JNI interface, including create new instance, call method, get/set fields, define constants.

**Glue** stands for c++ code to implement Java native method. (Glue java and C++.)
**Proxy** stands for c++ class to provide calls to java from c++. (c++ side proxy for the java class.)

And there is an extra bonus ['jnihelper.h'](cpp/jnihelper.h) that uses C++ RAII technology to simplify JNI APIs. When opt-in (with `'jenny.useJniHelper'=true`), the generated proxy class will also add methods using `jnihelper`, which makes life even happier!

---

**Table Of Content:**
[TOC]

---

## At a glance

Let's see what the generated code is.

You can find full code in [sample-gen]().

### Glue

Java class.

```java
@NativeClass
public class NativeTest {
    public native int add(int a, int b);
    public native void cpp_magic(String s, byte[] data);
}
```

The generated Glue code.

```C++
// NativeTest.h

namespace NativeTest {
static constexpr auto FULL_CLASS_NAME = u8"io/github/landerlyoung/jennysampleapp/NativeTest";

jint JNICALL add(JNIEnv* env, jobject thiz, jint a, jint b);
void JNICALL cpp_magic(JNIEnv* env, jobject thiz, jstring s, jbyteArray data);

inline bool registerNativeFunctions(JNIEnv* env) { ... }

}

// NativeTest.cpp

jint NativeTest::add(JNIEnv* env, jobject thiz, jint a, jint b) {
    // TODO(jenny): generated method stub.
    return 0;
}

void NativeTest::cpp_magic(JNIEnv* env, jobject thiz, jstring s, jbyteArray data) {
    // TODO(jenny): generated method stub.
}

```

Jenny generate:

1. constant defines
2. JNI register function
3. native method declare
4. native method implementation stubs

You just need to fill the stub with real code.

### Proxy

The following code is a show case that C++ uses OkHttp to perfomr a HTTP get operation through JNI APIs.

```C++
jstring func(jstring _url) {
    jenny::LocalRef<jstring> url(_url, false);

    OkHttpClientProxy client = OkHttpClientProxy::newInstance();
    BuilderProxy builder = BuilderProxy::newInstance().url(url);
    RequestProxy request = builder.build();
    CallProxy call = client.newCall(request.getThis());
    ResponseProxy response = call.execute();
    ResponseBodyProxy body = response.body();
    return body.string().release();
}
```
And here the equivlent java code.

```java
String run(String url) throws IOException {
    OkHttpClient client = new OkHttpClient();
    Request request = new Request.Builder()
        .url(url)
        .build();

    Response response = client.newCall(request).execute();
    return response.body().string();
}
```

If you are femiliar with JNI, yuo'd be surprised! The C++ code using Jenny just as clean as the Java code. Without Jenny it would be nightmare.


And also, here is another example without `jnihelper`.

```C++
void NativeDrawable::draw(JNIEnv *env, jobject thiz, jobject _canvas) {
    auto bounds = GraphicsProxy::drawableGetBounds(env, thiz);

    GraphicsProxy::setColor(env, state->paint, state->color());
    GraphicsProxy::drawableCircle(
        env, _canvas,
        RectProxy::exactCenterX(env, bounds),
        RectProxy::exactCenterY(env, bounds),
        std::min(RectProxy::exactCenterX(env, bounds),
                 RectProxy::exactCenterY(env, bounds)) * 0.7f,
        state->paint
    );
}

```

## How to

### Use in gradle

Jenny is publish at [jcenter][BT].

```groovy

dependencies {
    compileOnly 'io.github.landerlyoung:jenny-annotation:1.0.0'
    kapt 'io.github.landerlyoung:jenny-compiler:1.0.0'
    // for non-kotlin project use:
    // annotationProcessor 'io.github.landerlyoung:jenny-compiler:1.0.0'
}
```

For kotlin project, you gonna need the `kotlin-kapt` plugin.

That's it!

The generated code directory depends on your compiler config, typically, for android project it's inside `build/generated/source/kapt/debug/jenny`, for java project it's `build/generated/sources/annotationProcessor/java/main/jenny`.

You can use the generated code as you like, copy-past manually, or use gradle to copy them automatically (see sample in `sample-android/guild.gradle`).

### Use annotations

#### Annotations for glue

Add `@NativeClass()` annotation to you native class in order to let Jenny spot you class, and then generate corresponding cpp source.

Then Jenny would generate code for you, like here [sample-gen](sample-gen).

Note: There is a config field in `NativeClass.dynamicRegisterJniMethods`, when `true` (the default value) will generate code registering JNI function dynamically on the JNI_OnLoad callback by `JNIEnv::RegisterNatives`, instead of using JNI function name conversions (what javah/javac does).

#### Annotations for proxy

Add `@NativeProxy` to your normal java/kotlin class, need to cooperate with `@NativeMethodProxy` and `@NativeFieldProxy`, please read the doc.

Also, you can tell Jenny to generate code for libray classes by using the `@NativeProxyForClasses` annotation. ^[note]^

> note: Use this feature with caution. When your compile class path and runtime class path have different version of the same class, it's easy to crash because the proxy can't find some method which appears in compile class path but not on runtime class path. For instance to generate proxy for [`java.net.http.HttpRequest`](https://docs.oracle.com/en/java/javase/11/docs/api/java.net.http/java/net/http/HttpRequest.html) compiled with java-11, ran with java-7, your code crashes because that class just don't exist in java-7.
>
> In this case, the recommand way is to write your own helper class, and generate proxy for it.

## Configurations

Jenny annotation processor arguments:

| name | default value | meaning |
| :-: | :-: | :-: | 
| `jenny.threadSafe` | `true` | 
| `jenny.errorLoggerFunction` | `null` | 
| `jenny.outputDirectory` | `null` |
| `jenny.fusionProxyHeaderName` | `"jenny_fusion_proxies.h"` |
| `jenny.headerOnlyProxy` | `true` |
| `jenny.useJniHelper` | `false` |

And also, there are some config in Jenny's annotations, please read the doc.

## FAQ

#### How to passing arguments to annotation processor

1. For kotlin project, it simple

```groovy
kapt {
    // pass configurations to jenny
    arguments {
        arg("jenny.threadSafe", "false")
        arg("jenny.errorLoggerFunction", "jennySampleErrorLog")
        arg("jenny.outputDirectory", project.buildDir.absolutePath+"/test")
        arg("jenny.headerOnlyProxy", "true")
        arg("jenny.useJniHelper", "true")
        arg("jenny.fusionProxyHeaderName", "JennyFisonProxy.h")
    }
}
```

2. For Android, you can also [do this](https://developer.android.com/studio/build/dependencies#processor-arguments).

3. For Java Project, do this:

```groovy
compileJava {
    options.compilerArgs += [
            "-Ajenny.threadSafe=false",
            "-Ajenny.useJniHelper=false",
    ]
}
```