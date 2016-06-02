package com.young.jenny.annotation;

/**
 * Author: landerlyoung@gmail.com
 * Date:   2016-06-02
 * Time:   19-59
 * Life with Passion, Code with Creativity.
 *
 * mark a class/interface that is will be called by native code
 */
public @interface NativeReflect {
    /**
     * should jenny generate native glue code for all method,
     * inside this class/interface.
     */
    boolean allMethods() default false;
}
