package com.young.jenny.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Author: landerlyoung@tencent.com
 * Date:   2016-06-02
 * Time:   20-01
 * Life with Passion, Code with Creativity.
 * <p/>
 * If a class is marked with {@link NativeReflect @NativeReflace(allMethos = flse)},
 * jenny will generate glue code for methods marked with this Annotation in that class.
 * <p/>
 * If a class is marked with {@link NativeReflect @NativeReflace(allMethos = true)},
 * jenny will generate glue code for all methods in that class, regardless of this Annotation.
 *
 * @see NativeReflect
 */
@Retention(RetentionPolicy.SOURCE)
@Target({ElementType.METHOD, ElementType.CONSTRUCTOR})
public @interface NativeReflectMethod {
}
