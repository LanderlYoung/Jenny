package com.young.util.jni.generator;

import java.lang.annotation.Annotation;
import java.lang.reflect.Proxy;

/**
 * Author: taylorcyang@tencent.com
 * Date:   2016-06-19
 * Time:   15:45
 * Life with Passion, Code with Creativity.
 */

public class AnnotationResolver {
    @SuppressWarnings("unchecked")
    public static <T extends Annotation> T getDefaultImplementation(Class<T> annotation) {
        return (T) Proxy.newProxyInstance(
                annotation.getClassLoader(),
                new Class[]{annotation},
                (proxy, method, args) -> method.getDefaultValue());
    }
}
