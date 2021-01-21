/**
 * Copyright 2016 landerlyoung@gmail.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
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
 * <table>
 *     <caption>Jenny annotation-processor params</caption>
 *     <thead>
 *         <tr>
 *             <th>name</th>
 *             <th>default value</th>
 *             <th>meaning</th>
 *         </tr>
 *     </thead>
 *     <tbody>
 *         <tr>
 *             <td><code>jenny.threadSafe</code></td>
 *             <td><code>true</code></td>
 *             <td>The proxy class supports lazy init, this flag controls if the lazy init is thread safe or not.</td>
 *         </tr>
 *         <tr>
 *             <td><code>jenny.errorLoggerFunction</code></td>
 *             <td><code>null</code></td>
 *             <td>When proxy failed to find some method/class/field use the given function to do log before abort. The function must be a C++ function on top namespace with signature as <code>void(JNIEnv* env, const char* error)</code></td>
 *         </tr>
 *         <tr>
 *             <td><code>jenny.outputDirectory</code></td>
 *             <td><code>null</code></td>
 *             <td>By default, Jenny generate filed to apt dst dir, use this argument to control where the generated files are.</td>
 *         </tr>
 *         <tr>
 *             <td><code>jenny.fusionProxyHeaderName</code></td>
 *             <td><code>jenny_fusion_proxies.h</code></td>
 *             <td>The <code>fusionProxyHeader</code> is a header file that include all generated proxy files and gives you a <code>jenny::initAllProxies</code> function to init all proxies at once, this flag changes the file name.</td>
 *         </tr>
 *         <tr>
 *             <td><code>jenny.headerOnlyProxy</code></td>
 *             <td><code>true</code></td>
 *             <td>The generated proxy file use header only fusion or not.</td>
 *         </tr>
 *         <tr>
 *             <td><code>jenny.useJniHelper</code></td>
 *             <td><code>false</code></td>
 *             <td>Turn on/off jnihelper</td>
 *         </tr>
 *     </tbody>
 * </table>
 *
 * <hr>
 *
 * <pre>
 * Author: landerlyoung@gmail.com
 * Date:   2016-06-02
 * Time:   19-59
 * Life with Passion, Code with Creativity.
 * </pre>
 *
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
