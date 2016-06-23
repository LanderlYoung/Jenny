package com.young.jenny.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

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
    /**
     * generate android log marcos
     */
    boolean androidLog() default true;

    /**
     * use simple class name instead of full java class name for
     * generated file name or cpp class name
     */
    boolean simpleName() default true;

    /**
     * this will override {@link #simpleName()}, and force
     * the output file be the given name
     */
    String fileName() default "";

    /**
     * register JNI function dynamically on the JNI_OnLoad callback,
     * instead of use JNI function name conversions.
     *
     * The dynamic way can hide your shared library export functions,
     * so it is recommended.
     */
    boolean dynamicRegisterJniMethods() default true;
}
