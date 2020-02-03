/**
 * Copyright 2016 landerlyoung@gmail.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.github.landerlyoung.jenny;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Control whether should Jenny generate proxy getter/setter functions for this field.
 * Regardless of the {@link NativeProxy @NativeProxy(allFields = flse) or @NativeProxy(allFields = true)} config.
 * <br>
 * <hr>
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
 * But Jenny tries to be helpful, so she logs a warning in the console,
 * and pray to god that you might see it.
 * However, you can still indicate Jenny to generate getter/setter to that field,
 * in case you may remove the final keyword latter or just want to make die.
 * The choice is on you.
 *
 * <hr>
 *
 * <pre>
 * Author: landerlyoung@gmail.com
 * Date:   2016-06-02
 * Time:   20-52
 * Life with Passion, Code with Creativity.
 * </pre>
 *
 * @see NativeProxy#allFields()
 */
@Retention(RetentionPolicy.CLASS)
@Target(ElementType.FIELD)
public @interface NativeFieldProxy {
    /**
     * @return generate getter function or not
     */
    boolean getter() default true;

    /**
     * @return generate setter function or not
     */
    boolean setter() default true;
}
