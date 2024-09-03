// Copyright (C) 2024 The Qt Company Ltd.

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