package io.github.landerlyoung.jenny

import javax.lang.model.element.TypeElement

data class JteData(
    val mCppClassName: String,
    val mSimpleClassName: String,
    val mNamespaceHelper: NamespaceHelper,
    val mSlashClassName: String,
    val mEnv: Environment,
    var param: String,
    var returnType: String,
    var methodPrologue: String,
    var isStatic: Boolean = false,
    var useJniHelper: Boolean = false,
    var clazz: TypeElement? = null,
    var method: MethodOverloadResolver.MethodRecord? = null,
    val mHelper: HandyHelper
)