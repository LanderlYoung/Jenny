package com.young.jenny.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Method;

/**
 * Author: landerlyoung@gmail.com
 * Date:   2014-12-17
 * Time:   9:59
 * Life with passion. Code with creativity!
 * <p>
 * <p>
 * An annotation to mark an class as native, thus causing
 * {@link com.young.util.jni.generator.JNICppSourceGenerateProcessor}
 * to process annotations and to generate corresponding JNI header and source
 * file.
 * </p>
 * <p>
 * <b>note</b>: The @{code arch} value is currently not used.
 * <p>
 */
@Retention(RetentionPolicy.SOURCE)
@Target(ElementType.TYPE)
public @interface NativeClass {
    boolean androidLog() default true;
}
