package io.github.landerlyoung.jenny

import javax.lang.model.element.TypeElement

data class JteData(
    val className: String,
    val simpleClassName: String,
    val namespaceHelper: NamespaceHelper,
    val slashClassName: String,
    val environment: Environment,
    var param: String = "",
    var returnType: String = "",
    var methodPrologue: String = "",
    var isStatic: Boolean = false,
    var useJniHelper: Boolean = false,
    var clazz: TypeElement? = null,
    var method: MethodOverloadResolver.MethodRecord? = null,
    val handyHelper: HandyHelper
)