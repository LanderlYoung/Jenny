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
 * Regardless of the {@link NativeProxy @NativeProxy(allFields = flse) or @NativeProxy(allFields = true)} config.
 * <p/>
 * <p>
 * <hr/>
 * If a field is not annotated with this Annotation,
 * and @NativeProxy(allFields = true) for the enclosing class is set,
 * the "auto" strategy is applied.
 * The algorithm of "auto" is described below:
 * <ul>
 * <li> If you already have a set&lt;FieldName&gt; method, Jenny will not generate setter. </li>
 * <li> If you already have a get&lt;FieldName&gt; method, or is&lt;FieldName&gt; for boolean field,
 * Jenny will not generate getter. </li>
 * </ul>
 * <p>
 * NOTE: for compile-time constant field who is not annotated with this Annotation,
 * Jenny behaves like it were annotated with @NativeFieldProxy(getter = false, setter = false).
 * Since, in common case, there is no meaning to change a compile-time constant,
 * because all code referencing to it get inlined when compile.
 * And if you really do that to a "static" compile-time constant,
 * the jvm will gracefully raise an {@link IllegalAccessException}.
 *
 * But Jenny tries to be help, so she logs a warning in the console,
 * and pray to god that you might see it.
 * However, you can still indicate Jenny to generate getter/setter to that field,
 * in case you may remove the final keyword latter or just want to make die.
 * The choice is on you.
 *
 * @see NativeProxy#allFields()
 */
@Retention(RetentionPolicy.SOURCE)
@Target(ElementType.FIELD)
public @interface NativeFieldProxy {
    /** generate getter function or not */
    boolean getter() default false;

    /** generate setter function or not */
    boolean setter() default false;
}
