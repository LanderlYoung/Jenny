package com.young.jenny.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Author: landerlyoung@gmail.com
 * Date:   2016-06-02
 * Time:   19-59
 * Life with Passion, Code with Creativity.
 *
 * mark a class/interface that is will be called by native code
 */
@Retention(RetentionPolicy.SOURCE)
@Target(ElementType.TYPE)
public @interface NativeReflect {
    /**
     * should jenny generate native glue code for all method,
     * inside this class/interface.
     */
    boolean allMethods() default true;

    boolean allFields() default true;
}
