#Jenny

**JNI glue code generator**

This is a java annotation processor, which helps you generate C/C++ code for JNI calls according to your java native class.

###Source structure
source code contains two Annotation classes: `NativeClass` and `NativeSource`, and a Annotation processor.

###How to use it

####First

Add `@NativeClass()`annotation to you native class in order to help Annotation Processor spot you class, and then generate corresponding cpp source.

You can also add `@NativeSource("Cpp code")` to native method, then the generator will fill you cpp function with given code.

sample:

```java
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


####Second

Process you java code with Java Annotation Processor.

The Processor class is `com.young.util.jni.generator.JNICppSourceGenerateProcessor`. You can pass the processor to your javac command with switch `-processor`, like `javac -classpath Jenny.jar -d out/ -processor "com.young.util.jni.generator.JNICppSourceGenerateProcessor"  com/young/test/NativeTest.java`.

See test for more details.


If you are using IDEs like IntelliJ IDEA or Eclipse, google it to see how to add annotation processors.

###2.See it's power

By default, Jenny will generate .h file and .cpp file for each class,  and each class has it's own `JNI_OnLoad` and `JNI_OnUnload`. In `JNI_OnLoad`, a function named register_<java class name> function will be called to register native functions. So, if you want integrate them into one dynamic library(.dll in windows, .so in linux/unix, .dylib in OSX), just eliminate those two functions in you cpp, and keep one pair of them in one dynamic library, and remember to gerister your native methods.


####have fun with Jenny .^_^.

