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
 * <p>
 *      Fill the native function body with given cpp code! Cool!
 * </p>
 * <p>
 *     This Annotation is optional, and can only be applied to native method.
 * </p>
 *
 * example:
 * <pre>
 * {@literal @}NativeCode({
 *     "jint c = a + b;",
 *     "return c;"
 * })
 * public native int addInNative(int a, int b);
 * </pre>
 *
 * <hr>
 * <pre>
 * Author: landerlyoung@gmail.com
 * Date:   2014-12-16
 * Time:   19:36
 * Life with passion. Code with creativity!
 * </pre>
 */
@Retention(RetentionPolicy.SOURCE)
@Target(ElementType.METHOD)
public @interface NativeCode {
    /** source code */
    String[] value() default "";
}
