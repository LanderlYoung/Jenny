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

import java.io.IOException
import java.util.LinkedList
import javax.lang.model.element.ElementKind
import javax.lang.model.element.ExecutableElement
import javax.lang.model.element.Modifier
import javax.lang.model.element.TypeElement
import javax.lang.model.element.VariableElement
import javax.tools.StandardLocation

/**
 * Author: landerlyoung@gmail.com
 * Date:   2014-12-17
 * Time:   16:03
 * Life with passion. Code with creativity!
 */

//DONE HandyHelper.toJNIType throwable
//DONE Use NativeClass to mark generate, NativeCode to add implements
//DELETE different constant value for different arch
//DONE fix get package name
//DONE support for inner class
//XXXX support for pure c code
//DONE file output
//GOING use file template
class NativeGlueGenerator(env: Environment, clazz: TypeElement) : AbsCodeGenerator(env, clazz) {
    private lateinit var mNamespaceHelper: NamespaceHelper

    // header file name
    private lateinit var mHeaderName: String

    // source file Name
    private lateinit var mSourceName: String
    private val mMethods: MutableList<MethodOverloadResolver.MethodRecord> = LinkedList()
    private val mNativeClassAnnotation: NativeClass =
            clazz.getAnnotation(NativeClass::class.java)
                    ?: AnnotationResolver.getDefaultImplementation(NativeClass::class.java)

    private val cppClassName: String = mSimpleClassName

    override fun doGenerate(): CppClass {
        if (init() && mMethods.isNotEmpty()) {
            generateHeader()
            generateSource()
        }
        return CppClass(cppClassName, mNamespaceHelper.namespaceNotation, mHeaderName)
    }

    private fun init(): Boolean {
        if (mClazz.kind != ElementKind.CLASS) return false

        mNamespaceHelper = NamespaceHelper(mNativeClassAnnotation.namespace)

        mHeaderName = mNamespaceHelper.fileNamePrefix + "$cppClassName.h"
        mSourceName = mNamespaceHelper.fileNamePrefix + "$cppClassName.cpp"
        log("jenny begin generate glue code for class [$mClassName]")
        log("header : [$mHeaderName]")
        log("source : [$mSourceName]")

        findNativeMethods()

        return true
    }

    private fun findNativeMethods() {
        mClazz.enclosedElements
                .asSequence()
                .filter { it.kind == ElementKind.METHOD }
                .map { e ->
                    if (!e.modifiers.contains(Modifier.NATIVE) && e.getAnnotation(NativeCode::class.java) != null) {
                        error("Annotation @" + NativeCode::class.java.simpleName
                                + " should only be applied to NATIVE method! found at:${mClassName}.${e}")
                    }
                    e as ExecutableElement
                }.filter {
                    it.modifiers.contains(Modifier.NATIVE)
                }.groupBy { it.simpleName.toString() }.forEach { (_, methods) ->
                    MethodOverloadResolver(mHelper) { "" }.resolve(methods).let {
                        mMethods.addAll(it)
                    }
                }
    }

    private fun generateHeader() {
        log("write header file [$mHeaderName]")

        mEnv.createOutputFile(Constants.JENNY_GEN_DIR_GLUE_HEADER, mHeaderName).use { out ->
            try {
                buildString {
                    append(Constants.AUTO_GENERATE_NOTICE)
                    append("""
                        |
                        |/* C++ header file for class $mSlashClassName */
                        |#pragma once
                        |
                        |#include <jni.h>
                        |
                        |${mNamespaceHelper.beginNamespace()}
                        |namespace $cppClassName {
                        |
                        |// DO NOT modify
                        |static constexpr auto FULL_CLASS_NAME = u8"$mSlashClassName";
                        |
                        |""".trimMargin())

                    if (mNativeClassAnnotation.dynamicRegisterJniMethods) {
                        buildConstantsDefinition()
                        buildMethodsDefinition(false)
                        buildJniRegister()
                        endNamespace()
                    } else {
                        buildConstantsDefinition()
                        endNamespace()
                        buildMethodsDefinition(false)
                    }
                }.let { content ->
                    out.write(content.toByteArray(Charsets.UTF_8))
                }
            } catch (e: IOException) {
                warn("generate header file $mHeaderName failed!")
            }
        }
    }

    private fun generateSource() {
        log("write source file [$mSourceName]")
        try {
            buildString {
                append(Constants.AUTO_GENERATE_SOURCE_NOTICE)
                append("""
                    |#include "$mHeaderName"
                    |
                    |""".trimMargin())

                if (mNativeClassAnnotation.dynamicRegisterJniMethods) {
                    append("""
                        |
                        |${mNamespaceHelper.beginNamespace()}
                        |
                        |""".trimMargin())
                }

                buildMethodsDefinition(true)

                if (mNativeClassAnnotation.dynamicRegisterJniMethods) {
                    endNamespace(true)
                }
            }.let { content ->
                mEnv.createOutputFile(Constants.JENNY_GEN_DIR_GLUE_SOURCE, mSourceName).use {
                    it.write(content.toByteArray(Charsets.UTF_8))
                }
            }
        } catch (e: IOException) {
            warn("generate source file $mSourceName failed")
        }
    }

    private fun StringBuilder.endNamespace(isSource: Boolean = false) {
        append('\n')
        if (!isSource) {
            append("} // endof namespace $cppClassName\n")
        }
        append("${mNamespaceHelper.endNamespace()}\n\n")
    }

    private fun StringBuilder.buildConstantsDefinition() {
        mClazz.enclosedElements
                .asSequence()
                .filter { it.kind.isField && it.modifiers.containsAll(listOf(Modifier.STATIC, Modifier.FINAL)) }
                .map { it as VariableElement }
                .filter {
                    // if this field is a compile-time constant value it's
                    // value will be returned, otherwise null will be returned.
                    it.constantValue != null
                }
                .forEach { append("${mHelper.getConstexprStatement(it)}\n") }
        append('\n')
    }

    private fun StringBuilder.buildMethodsDefinition(isSource: Boolean) {
        val externC = !isSource && !mNativeClassAnnotation.dynamicRegisterJniMethods
        if (externC) {
            append("""
                |
                |#ifdef __cplusplus
                |extern "C" {
                |#endif
                |
                |""".trimMargin())
        }
        mMethods.forEach { m ->
            val e = m.method
            val javaModifiers = mHelper.getModifiers(e)
            val javaReturnType = e.returnType.toString()
            val javaMethodName = e.simpleName.toString()
            val javaParameters = mHelper.getJavaMethodParam(e)
            val javaMethodSignature = mHelper.getBinaryMethodSignature(e)
            val export = if (isSource || mNativeClassAnnotation.dynamicRegisterJniMethods) "" else "JNIEXPORT "
            val jniCall = if (isSource) "" else "JNICALL "
            val jniReturnType = mHelper.toJNIType(e.returnType)
            val nativeMethodName =
                    if (isSource && mNativeClassAnnotation.dynamicRegisterJniMethods)
                        cppClassName + "::" + getMethodName(e) + m.resolvedPostFix
                    else
                        getMethodName(e) + m.resolvedPostFix
            val nativeParameters = mHelper.getNativeMethodParam(e)

            append("""
            |/*
            | * Class:     $mClassName
            | * Method:    $javaModifiers $javaReturnType ${javaMethodName}(${javaParameters})
            | * Signature: $javaMethodSignature
            | */
            |${export}${jniReturnType} ${jniCall}${nativeMethodName}(${nativeParameters})""".trimMargin())

            if (isSource) {
                buildMethodBodyWithReturnStatement(e)
            } else {
                append(';')
            }
            append("\n\n")
        }

        if (externC) {
            append("""|#ifdef __cplusplus
                      |}
                      |#endif""".trimMargin())
        }
    }

    private fun StringBuilder.buildMethodBodyWithReturnStatement(m: ExecutableElement) {
        append(" {\n")
        val a = m.getAnnotation(NativeCode::class.java)
        if (a != null) {
            for (line in a.value) {
                append("    ")
                append(line)
                append('\n')
            }
        } else {
            append("    ")
            append(mHelper.getReturnStatement(m))
            append('\n')
        }
        append("}")
    }

    private fun StringBuilder.buildJniRegister() {
        append("""
            |/**
            |* register Native functions
            |* @returns success or not
            |*/
            |inline bool registerNativeFunctions(JNIEnv* env) {
            |// 1. C++20 has u8"" string as char8_t type, we should cast them.
            |// 2. jni.h has JNINativeMethod::name as char* type not const char*. (while Android does)
            |#define jenny_u8cast(u8) const_cast<char *>(reinterpret_cast<const char *>(u8))
            |   const JNINativeMethod gsNativeMethods[] = {
            |""".trimMargin())
        buildJniNativeMethodStructs()
        append("""
            |   };
            |
            |   const int gsMethodCount = sizeof(gsNativeMethods) / sizeof(JNINativeMethod);
            |
            |   bool success = false;
            |   jclass clazz = env->FindClass(jenny_u8cast(FULL_CLASS_NAME));
            |   if (clazz != nullptr) {
            |       success = !env->RegisterNatives(clazz, gsNativeMethods, gsMethodCount);
            |       env->DeleteLocalRef(clazz);
            |   }
            |   return success;
            |#undef jenny_u8cast
            |}
            |""".trimMargin())
    }

    private fun StringBuilder.buildJniNativeMethodStructs() {
        val it = mMethods.iterator()
        while (it.hasNext()) {
            val m = it.next()
            val methodName = m.method.simpleName.toString()
            val symbol = getMethodName(m.method) + m.resolvedPostFix
            val signature = mHelper.getBinaryMethodSignature(m.method)
            append("""
            |       {
            |           /* method name      */ jenny_u8cast(u8"$methodName"),
            |           /* method signature */ jenny_u8cast(u8"$signature"),
            |           /* function pointer */ reinterpret_cast<void *>(${symbol})
            |       }""".trimMargin())
            if (it.hasNext()) {
                append(",")
            }
            append('\n')

        }
    }

    private fun getMethodName(m: ExecutableElement): String {
        val simpleName = m.simpleName.toString()
        return if (mNativeClassAnnotation.dynamicRegisterJniMethods) {
            simpleName
        } else {
            "Java_" + mJNIClassName + "_" + simpleName.replace("_", "_1").stripNonASCII()
        }
    }

}
