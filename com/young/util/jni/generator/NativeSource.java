package com.young.util.jni.generator;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Author: LanderlYoung
 * Date:   2014-12-16
 * Time:   19:36
 * Life with passion. Code with creativity!
 */
@Retention(RetentionPolicy.SOURCE)
@Target(ElementType.METHOD)
public @interface NativeSource {
    String[] value() default "";
}
