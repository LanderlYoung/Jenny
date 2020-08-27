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
 * Mark a class/interface that will be called by native code.
 * So that, jenny will generate cpp proxy class for you to access
 * java object with ease.
 * Jenny does the reflect work, and leave you with a clean world!
 *
 * <hr>
 * <pre>
 * Author: landerlyoung@gmail.com
 * Date:   2016-06-02
 * Time:   19-59
 * Life with Passion, Code with Creativity.
 * </pre>

 */
@Retention(RetentionPolicy.CLASS)
@Target(ElementType.TYPE)
public @interface NativeProxy {
    /**
     * @return should jenny generate native glue code for all methods
     * inside this class/interface.
     */
    boolean allMethods() default false;

    /**
     * @return should jenny generate native glue code for all fields
     * inside this class/interface.
     */
    boolean allFields() default false;

    /**
     * @return C++ namespace for generated class
     */
    String namespace() default "";
}
