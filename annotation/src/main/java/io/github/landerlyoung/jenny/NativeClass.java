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
 * An annotation to mark a class as native, thus causing
 * Jenny Annotation processor to process annotations
 * and to generate corresponding JNI header and source files.
 * </p>
 *
 * <hr>
 * <pre>
 * Author: landerlyoung@gmail.com
 * Date:   2014-12-17
 * Time:   9:59
 * Life with passion. Code with creativity!
 * </pre>
 */
@Retention(RetentionPolicy.SOURCE)
@Target(ElementType.TYPE)
public @interface NativeClass {
    /**
     * generate android log marcos
     */
    boolean androidLog() default true;

    /**
     * use simple class name instead of full java class name for
     * generated file name or cpp class name
     */
    boolean simpleName() default true;

    /**
     * this will override {@link #simpleName()}, and force
     * the output file be the given name
     */
    String fileName() default "";

    /**
     * register JNI function dynamically on the JNI_OnLoad callback,
     * instead of use JNI function name conversions.
     * <p>
     * The dynamic way can hide your shared library export functions,
     * so it is recommended.
     */
    boolean dynamicRegisterJniMethods() default true;
}
