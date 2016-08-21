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
package io.github.landerlyoung.jennysample;

import java.util.HashMap;

/**
 * Author: landerlyoung@gmail.com
 * Date:   2016-06-19
 * Time:   21:51
 * Life with Passion, Code with Creativity.
 */

public class Main {
    public static void main(String[] args) {
        ComputeInNative engine = new ComputeInNative();
        engine.init();
        engine.setParam(new HashMap<>());
        engine.request("{req:0}", ((success, rsp) -> {
            System.out.println("success=" + success + ", rsp=" + rsp);
        }));
        engine.release();
    }
}
