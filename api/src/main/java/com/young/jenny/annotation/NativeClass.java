package com.young.jenny.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Author: LanderlYoung
 * Date:   2014-12-17
 * Time:   9:59
 * Life with passion. Code with creativity!
 *
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
     * 32bit architecture
     */
    int ARCH_32 = -1;

    /**
     * 64bit architecture
     */
    int ARCH_64 = -2;

    /**
     * target machine architecture,
     * the default is {@link #ARCH_32}
     * You should apply an architecture for the native source that this class related to.
     * the <B>ONLY</B> different between different architecture in the generated cpp file
     * is in the {@code type long} CONSTANTs value. eg, long type constant {@code 0} in 32bit cpp is 0LL literally,
     * but which is 32L (or 32i64) in 64bit architecture.
     *
     * @see
     */
    int arch() default ARCH_32;
}
