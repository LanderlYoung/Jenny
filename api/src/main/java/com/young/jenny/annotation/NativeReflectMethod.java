package com.young.jenny.annotation;

/**
 * Author: taylorcyang@tencent.com
 * Date:   2016-06-02
 * Time:   20-01
 * Life with Passion, Code with Creativity.
 *
 * If a class is marked with {@link NativeReflect @NativeReflace(allMethos = flse)},
 * jenny will generate glue code for methods marked with this Annotation in that class.
 *
 * If a class is marked with {@link NativeReflect @NativeReflace(allMethos = true)},
 * jenny will generate glue code for all methods in that class, regardless of this Annotation.
 * @see NativeReflect
 */
public @interface NativeReflectMethod {
}
