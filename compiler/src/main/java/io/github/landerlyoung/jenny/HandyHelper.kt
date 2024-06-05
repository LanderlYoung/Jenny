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
package io.github.landerlyoung.jenny

import java.util.ArrayDeque
import java.util.Locale
import javax.lang.model.element.Element
import javax.lang.model.element.ElementKind
import javax.lang.model.element.ExecutableElement
import javax.lang.model.element.Modifier
import javax.lang.model.element.TypeElement
import javax.lang.model.element.VariableElement
import javax.lang.model.type.ArrayType
import javax.lang.model.type.DeclaredType
import javax.lang.model.type.IntersectionType
import javax.lang.model.type.NoType
import javax.lang.model.type.PrimitiveType
import javax.lang.model.type.TypeKind
import javax.lang.model.type.TypeMirror
import javax.lang.model.type.TypeVariable
import javax.lang.model.type.WildcardType

/**
 * Author: landerlyoung@gmail.com
 * Date:   2014-12-17
 * Time:   20:19
 * Life with passion. Code with creativity!
 */
class HandyHelper(private val mEnv: Environment) {

    fun getBinaryMethodSignature(method: ExecutableElement): String {
        return Signature(method).toString()
    }

    fun getBinaryTypeSignature(type: TypeMirror): String {
        return Signature(type).toString()
    }

    /**
     *
     * example
     * Signature: (ILjava/lang/Runnable;LN/M_M;)V
     * JNIEXPORT void JNICALL Java_N_n__ILjava_lang_Runnable_2LN_M_1M_2
     *
     * __ILjava_lang_Runnable_2LN_M_1M_2
     */
    fun getMethodOverloadPostfix(method: ExecutableElement): String {
        val signature = getBinaryMethodSignature(method)
        val paramSig = signature.subSequence(signature.indexOf('(') + 1, signature.indexOf(")")).toString()
        return "__" + paramSig.replace("_", "_1")
                .replace("/", "_")
                .replace(";", "_2")
                .stripNonASCII()
    }

    /**
     * @return like com.example_package.SomeClass$InnerClass
     */
    fun getClassName(clazz: Element): String {
        val className = ArrayDeque<String>()
        val sb = StringBuilder()
        var e: Element? = clazz

        while (e != null && (e.kind.isClass || e.kind.isInterface)) {
            className.add(e.simpleName.toString())
            e = e.enclosingElement
        }

        val pkg = mEnv.elementUtils.getPackageOf(clazz)
        if (pkg != null) {
            val pkgName = pkg.qualifiedName.toString()
            if (pkgName.isNotEmpty()) {
                sb.append(pkgName)
                sb.append('.')
            }
        }

        while (!className.isEmpty()) {
            sb.append(className.removeLast())
            sb.append('$')
        }
        sb.deleteCharAt(sb.length - 1)
        return sb.toString()
    }

    /**
     * @return like com/example_package/SomeClass$InnerClass
     */
    fun getSlashClassName(className: String): String {
        return className.replace('.', '/')
    }

    /**
     * @param className
     *
     * @return like com_example_1package_SomeClass_InnerClass
     */
    fun toJNIClassName(className: String): String {
        return className.replace("_", "_1")
                .replace(".", "_")
                .stripNonASCII()
    }

    fun getModifiers(m: Element): String =
            m.modifiers.asSequence()
                    .filter { modifier ->
                        modifier == Modifier.PUBLIC
                                || modifier == Modifier.PROTECTED
                                || modifier == Modifier.PRIVATE
                                || modifier == Modifier.FINAL
                                || modifier == Modifier.STATIC
                                || modifier == Modifier.ABSTRACT
                                || modifier == Modifier.SYNCHRONIZED
                    }
                    .sorted()
                    .joinToString(" ") { it.toString().toLowerCase(Locale.US) }

    fun getJavaMethodParam(m: ExecutableElement) =
            m.parameters.joinToString(", ") {
                it.asType().toString() + " " + it.simpleName
            }

    fun getReturnStatement(e: ExecutableElement): String = buildString {
        val returnType = e.returnType
        if (returnType is NoType) {
            return@buildString
        }

        append("return ")
        if (returnType is PrimitiveType) {
            if (returnType.kind == TypeKind.BOOLEAN) {
                append("JNI_FALSE")
            } else {
                append("0")
            }
        } else if (returnType.toString() == String::class.java.name) {
            append("env->NewStringUTF(\"Hello From Jenny\")")
        } else {
            append("nullptr")
        }
        append(";")
    }

    fun getConstexprStatement(it: VariableElement): String {
        val constValue = it.constantValue!!

        val t = it.asType()
        val nativeType = if (t == null) null else {
            val jniType = toJNIType(t)
            if (jniType == "jstring") {
                "auto"
            } else jniType
        }

        val value = if (constValue is Boolean) {
            if (constValue) "JNI_TRUE" else "JNI_FALSE"
        } else if (constValue is Number) {
            constValue.toString()
        } else if (constValue is Char) {
            "'${constValue}'"
        } else if (constValue is String) {
            "u8\"$constValue\""
        } else {
            throw IllegalArgumentException("unknown type:$constValue " + constValue.javaClass)
        }

        return "static constexpr $nativeType ${it.simpleName} = ${value};"
    }

    fun getNativeMethodParam(m: ExecutableElement): String {
        val sb = StringBuilder()
        sb.append("JNIEnv* env")

        if (m.modifiers.contains(Modifier.STATIC)) {
            sb.append(", jclass clazz")
        } else {
            sb.append(", jobject thiz")
        }

        m.parameters.forEach { ve ->
            sb.append(", ")
            sb.append(toJNIType(ve.asType()))
            sb.append(' ')
            sb.append(ve.simpleName.toString())
        }
        return sb.toString()
    }

    fun isNestedClass(clazz: Element): Boolean {
        val enclosingElement: Element? = clazz.enclosingElement
        return (enclosingElement != null
                && enclosingElement.kind == ElementKind.CLASS
                && !clazz.modifiers.contains(Modifier.STATIC))
    }

    fun instanceOf(clazzName: String, typeMirror: TypeMirror): Boolean {
        var t = typeMirror
        while (clazzName != getNonGenericName(t)) {
            val base = mEnv.typeUtils.asElement(t)
            if (base is TypeElement) {
                val superClazz = base.superclass
                if (superClazz is NoType) return false
                t = superClazz
            } else {
                return false
            }
        }
        return true
    }

    fun getNonGenericName(t: TypeMirror): String = when (t) {
        is DeclaredType -> getClassName(t.asElement())
        is TypeVariable -> {
            // function param
            val upper = t.upperBound
            getNonGenericName(
                    if (upper is IntersectionType) {
                        upper.bounds[0]
                    } else {
                        upper
                    }
            )
        }
        is WildcardType -> {
            // function param
            t.extendsBound?.let {
                getNonGenericName(it)
            } ?: java.lang.Object::class.java.name
        }
        is ArrayType -> {
            getNonGenericName(t.componentType) + "[]"
        }
        is PrimitiveType -> t.toString()
        is NoType -> "void"
        else -> throw IllegalArgumentException("TypeMirror kind: ${t.kind} is not supported ${t.javaClass}")
    }

    fun getClassState(what: String): String {
        return "getClassInitState().$what"
    }


    fun toJNIType(t: TypeMirror?): String {
        if (t == null) return ""

        // check if t is a subclass of java.lang.Throwable
        if (instanceOf(Throwable::class.java.name, t)) {
            return "jthrowable"
        }

        return when (t) {
            is PrimitiveType -> "j${t.kind.name.toLowerCase(Locale.US)}"
            is NoType -> "void"
            is ArrayType -> {
                if (t.componentType is PrimitiveType) {
                    "j${t.componentType.kind.name.toLowerCase(Locale.US)}Array"
                } else {
                    "jobjectArray"
                }
            }
            else -> {
                if (t is DeclaredType) {
                    when (getClassName(t.asElement())) {
                        "java.lang.String" -> "jstring"
                        "java.lang.Class" -> "jclass"
                        else -> "jobject"
                    }
                } else {
                    "jobject"
                }
            }
        }
    }

    private inner class Signature(
            private val mMethod: ExecutableElement?,
            private val mType: TypeMirror?) {

        constructor(method: ExecutableElement) : this(method, null)

        constructor(type: TypeMirror) : this(null, type)

        private fun StringBuilder.getSignatureClassName(_type: TypeMirror) {
            var type = _type
            while (type is ArrayType) {
                append('[')
                type = type.componentType
            }
            when (val name = getNonGenericName(type)) {
                "char" -> append('C')
                "byte" -> append('B')
                "short" -> append('S')
                "int" -> append('I')
                "long" -> append('J')
                "float" -> append('F')
                "double" -> append('D')
                "boolean" -> append('Z')
                "void" -> append('V')
                else -> append('L').append(name.replace('.', '/')).append(';')
            }
        }

        override fun toString(): String = if (mMethod != null) {
            buildString {
                append('(')
                if (mMethod.simpleName.contentEquals("<init>")) {
                    val clazz = mMethod.enclosingElement
                    if (isNestedClass(clazz)) {
                        // generate this$0 param for nested class
                        val enclosingClazz = clazz.enclosingElement
                        getSignatureClassName(enclosingClazz.asType())
                    }
                }
                for (param in mMethod.parameters) {
                    getSignatureClassName(param.asType())
                }
                append(')')
                getSignatureClassName(mMethod.returnType)
                return toString()
            }
        } else {
            buildString { getSignatureClassName(mType!!) }
        }
    }
}

fun String.stripNonASCII(): String = this.replace("[^a-zA-Z0-9_]".toRegex()) {
    String.format(Locale.US, "_%05x", it.value.codePointAt(0))
}