package com.young.util.jni;

/**
 * Author: young
 * Date:   2014-10-16
 * Time:   下午10:35
 * Life with passion. Code with creativity!
 */
public class JNIHelper {
    public static String getJNIClassName(Class<?> c) {
        return toJNIClassName(c.getName());
    }

    /**
     * @param className
     * @return like com_example_1package_SomeClass_InnerClass
     */
    public static String toJNIClassName(String className) {
        if (className == null) return null;
        return className.replace("_", "_1")
                        .replace(".", "_")
                        .replace('$', '_'); //inner class
    }

    /**
     * @return like com/example_package/SomeClass$InnerClass
     */
    public static String getNativeSlashClassName(String className) {
        return className.replace('.', '/');
    }

    public static String toJNIType(Class<?> c) {
        if (c == null) {
            return "";
        } else if (c.isPrimitive()) {
            if (c != void.class) {
                return "j" + c.getName();
            } else {
                return "void";
            }
        } else if (c.isArray()) {
            if (c.getComponentType().isPrimitive()) {
                return "j" + c.getComponentType().getName() + "Array";
            } else {
                return "jobjectArray";
            }
        } else if (c == String.class) {
            return "jstring";
        } else if (Throwable.class.isAssignableFrom(c)) {
            return "jthrowable";
        } else if (c == Class.class) {
            return "jclass";
        } else {
            return "jobject";
        }
    }
}
