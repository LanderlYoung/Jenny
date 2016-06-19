package com.young.jenny.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Author: landerlyoung@gmail.com
 * Date:   2016-06-02
 * Time:   20-01
 * Life with Passion, Code with Creativity.
 * <p/>
 * Control whether should Jenny generate proxy function for this method/constructor.
 * Regardless of the {@link NativeProxy @NativeReflace(allMethos = flse) or @NativeReflace(allMethos = true)} config.
 * <p/>
 * @see NativeProxy
 */
@Retention(RetentionPolicy.SOURCE)
@Target({ElementType.METHOD, ElementType.CONSTRUCTOR})
public @interface NativeMethodProxy {
    boolean enabled() default true;
}
