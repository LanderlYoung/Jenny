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
package com.young.util.jni.generator;

import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;

/**
 * Author: landerlyoung@gmail.com
 * Date:   2014-12-18
 * Time:   10:02
 * Life with passion. Code with creativity!
 */
public final class Environment {
    public final Messager messager;
    public final Types typeUtils;
    public final Elements elementUtils;
    public final Filer filer;
    public final RoundEnvironment roundEnvironment;

    public Environment(Messager messager,
                       Types types,
                       Elements elements,
                       Filer filer,
                       RoundEnvironment environment) {
        this.messager = messager;
        this.typeUtils = types;
        this.elementUtils = elements;
        this.filer = filer;
        this.roundEnvironment = environment;
    }
}
