/**
 * Copyright (C) 2024 The Qt Company Ltd.
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

package io.github.landerlyoung.jenny

import javax.lang.model.element.TypeElement
import javax.lang.model.element.VariableElement

data class JteData(
    val className: String,
    val simpleClassName: String,
    val namespaceHelper: NamespaceHelper,
    val slashClassName: String,
    val environment: Environment,
    var param: String = "",
    var fieldSetterParam: String = "",
    var jniReturnType: String = "",
    var returnType: String = "",
    var methodPrologue: String = "",
    var staticMod: String = "",
    var constMod: String = "",
    var rawStaticMod: String = "",
    var rawConstMod: String = "",
    var classOrObj: String = "",
    var isStatic: Boolean = false,
    var static: String = "",
    var returnStatement: String = "",
    var wrapLocalRef: String = "",
    var returnTypeCast: String = "",
    var callExpressionClosing: String = "",
    var useJniHelper: Boolean = false,
    var clazz: TypeElement? = null,
    var method: MethodOverloadResolver.MethodRecord? = null,
    var field: VariableElement? = null,
    var fieldId: String = "",
    var fieldCamelCaseName: String = "",
    var fieldComment: String = "",
    var initClassPrefix: String = "",
    var initClassLockGuard: String = "",
    val handyHelper: HandyHelper
)