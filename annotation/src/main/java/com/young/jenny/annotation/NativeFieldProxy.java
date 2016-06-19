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
 * <p/>
 * Control whether should Jenny generate proxy getter/setter functions for this field.
 * Regardless of the {@link NativeProxy @NativeReflace(allFields = flse) or @NativeReflace(allFields = true)} config.
 * <p/>
 */
@Retention(RetentionPolicy.SOURCE)
@Target(ElementType.FIELD)
public @interface NativeFieldProxy {
    /** generate getter function or not */
    boolean getter() default false;

    /** generate setter function or not */
    boolean setter() default false;

    /**
     * Smart algorithm to check whether should Jenny generate getter or setter.
     * <ul>
     *     <li> If you already have a set&lt;FieldName&gt; method, Jenny will not generate setter. </li>
     *     <li> If you already have a get&lt;FieldName&gt; method, or is&lt;FieldName&gt; for boolean field,
     *     Jenny will not generate getter. </li>
     * </ul>
     */
    boolean auto() default true;
}
