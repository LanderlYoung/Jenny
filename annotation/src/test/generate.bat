del out
mkdir out
javac -classpath Jenny.jar -d out/ -processor "com.young.util.jni.generator.JennyAnnotationProcessor"  com/young/test/NativeTest.java
