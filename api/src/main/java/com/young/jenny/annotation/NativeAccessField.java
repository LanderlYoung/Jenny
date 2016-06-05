package com.young.jenny.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Author: landerlyoung@gmail.com
 * Date:   2016-06-02
 * Time:   20-52
 * Life with Passion, Code with Creativity.
 */
@Retention(RetentionPolicy.SOURCE)
@Target(ElementType.FIELD)
public @interface NativeAccessField {
    boolean getter() default false;

    boolean setter() default false;

    boolean auto() default true;
}
