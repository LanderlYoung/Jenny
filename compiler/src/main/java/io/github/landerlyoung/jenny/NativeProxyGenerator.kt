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
import java.util.EnumSet
import java.util.Locale
import javax.lang.model.element.Element
import javax.lang.model.element.ElementKind
import javax.lang.model.element.ExecutableElement
import javax.lang.model.element.Modifier
import javax.lang.model.element.TypeElement
import javax.lang.model.element.VariableElement
import javax.lang.model.type.NoType
import javax.lang.model.type.TypeKind
import javax.lang.model.type.TypeMirror
import kotlin.collections.component1
import kotlin.collections.component2

/**
 * Author: landerlyoung@gmail.com
 * Date:   2016-06-05
 * Time:   00:30
 * Life with Passion, Code with Creativity.
 */
class NativeProxyGenerator(env: Environment, clazz: TypeElement, nativeProxy: NativeProxyConfig) : AbsCodeGenerator(env, clazz) {

    data class NativeProxyConfig(
            val allMethods: Boolean,
            val allFields: Boolean,
            val namespace: String,
            val onlyPublic: Boolean) {
        constructor(proxy: NativeProxy)
                : this(proxy.allMethods, proxy.allFields, proxy.namespace, false)
    }

    //what we need to generate includes
    //---------- id ----------
    //constructor
    //method
    //field
    //-------- getXxxId -------
    //constructor
    //method
    //field
    //------- newInstance ------
    //constructor
    //------- callXxxMethod -----
    //method
    //------ get/setXxxField ----
    //field

    private val mConstructors = mutableListOf<MethodOverloadResolver.MethodRecord>()
    private val mMethodSimpleName = mutableSetOf<String>()
    private val mMethods = mutableListOf<MethodOverloadResolver.MethodRecord>()
    private val mFields = mutableListOf<VariableElement>()
    private val mConstants: MutableSet<VariableElement> = LinkedHashSet()
    private val mNativeProxyConfig = nativeProxy
    private val mNamespaceHelper = NamespaceHelper(mNativeProxyConfig.namespace)
    private val mHeaderName: String
    private val mSourceName: String

    private val cppClassName: String = mSimpleClassName + "Proxy"

    init {
        mHeaderName = mNamespaceHelper.fileNamePrefix + "${cppClassName}.h"
        mSourceName = mNamespaceHelper.fileNamePrefix + "${cppClassName}.cpp"
    }

    private fun init() {
        findConstants()
        findConstructors()
        findMethods()
        findFields()
    }

    override fun doGenerate(): CppClass {
        init()

        generatorHeader()
        if (!mEnv.configurations.headerOnlyProxy) {
            generateSource()
        }

        return CppClass(cppClassName, mNamespaceHelper.namespaceNotation, mHeaderName)
    }

    private fun generatorHeader() {
        mEnv.createOutputFile(Constants.JENNY_GEN_DIR_PROXY, mHeaderName).use { out ->
            try {
                log("write native proxy file [$mHeaderName]")
                buildString {
                    append(Constants.AUTO_GENERATE_NOTICE)
                    append("""
                        |#pragma once
                        |
                        |#include <jni.h>
                        |#include <assert.h>                        
                        |""".trimMargin())
                    if (mEnv.configurations.threadSafe) {
                        append("""
                        |#include <atomic>
                        |#include <mutex>
                        |
                        |""".trimMargin())
                    }
                    if (mEnv.configurations.useJniHelper) {
                        append("""
                        |#include "jnihelper.h"
                        |
                        |""".trimMargin())
                    }

                    append("""
                        |${mNamespaceHelper.beginNamespace()}
                        |class $cppClassName {
                        |
                        |public:
                        |    static constexpr auto FULL_CLASS_NAME = "$mSlashClassName";
                        |
                        |""".trimMargin())

                    buildConstantsIdDeclare()

                    append("""
                        |
                        |public:
                        |
                        |    static bool initClazz(JNIEnv* env);
                        |    
                        |    static void releaseClazz(JNIEnv* env);
                        |
                        |    static void assertInited(JNIEnv* env) {
                        |        auto initClazzSuccess = initClazz(env);
                        |        assert(initClazzSuccess);
                        |    }
                        |    
                        |""".trimMargin())

                    buildConstructorDefines(false)
                    buildMethodDefines(false)
                    buildFieldDefines(false)

                    if (mEnv.configurations.useJniHelper) {
                        generateForJniHelper()
                    }

                    append("""
                        |
                        |private:
                        |    struct ClassInitState {
                        |
                    """.trimMargin())

                    if (mEnv.configurations.threadSafe) {
                        append("""
                        |    // thread safe init
                        |    std::atomic_bool sInited {};
                        |    std::mutex sInitLock {};
                        |""".trimMargin())
                    } else {
                        append("    bool sInited = false;\n")
                    }
                    append("""
                        |
                        |    jclass sClazz = nullptr;
                        |
                    """.trimMargin())
                    buildConstructorIdDeclare()
                    buildMethodIdDeclare()
                    buildFieldIdDeclare()

                    append("""
                        |    }; // endof struct ClassInitState
                        |
                        |    static inline ClassInitState& getClassInitState() {
                        |        static ClassInitState classInitState;
                        |        return classInitState;
                        |    }
                        |
                        |
                    """.trimMargin())

                    append("};\n")
                    append(mNamespaceHelper.endNamespace())
                    append("\n\n")

                    if (mEnv.configurations.headerOnlyProxy) {
                        append("\n\n")
                        generateSourceContent(true)
                    }
                }.let { content ->
                    out.write(content.toByteArray(Charsets.UTF_8))
                }
            } catch (e: IOException) {
                warn("generate header file $mHeaderName failed!")
            }
        }
    }

    private fun StringBuilder.generateForJniHelper() {
        append("""
            |    // ====== jni helper ======
            |private:
            |    ::jenny::LocalRef<jobject> _local;
            |    ::jenny::GlobalRef<jobject> _global;
            | 
            |public:
            |
            |    // jni helper
            |    ::jenny::LocalRef<jobject> getThis(bool owned = true) const {
            |        if (_local) {
            |            if (owned) {
            |                return _local;
            |            } else {
            |                return ::jenny::LocalRef<jobject>(_local.get(), false);
            |            }
            |        } else {
            |            return _global.toLocal();
            |        }
            |    }
            |
            |    // jni helper constructors
            |    ${cppClassName}(jobject ref, bool owned = false): _local(ref, owned) {
            |       assertInited(::jenny::Env().get());
            |    }
            |   
            |    ${cppClassName}(::jenny::LocalRef<jobject> ref): _local(std::move(ref)) {
            |       assertInited(::jenny::Env().get());
            |    }
            |   
            |    ${cppClassName}(::jenny::GlobalRef<jobject> ref): _global(std::move(ref)) {
            |       assertInited(::jenny::Env().get());
            |    }
            |   
            |""".trimMargin())
        buildConstructorDefines(true)
        buildMethodDefines(true)
        buildFieldDefines(true)
    }

    private fun generateSource() {
        mEnv.createOutputFile(Constants.JENNY_GEN_DIR_PROXY, mSourceName)
                .use { out ->
                    try {
                        log("write native proxy file [$mSourceName]")
                        buildString {
                            generateSourceContent(false)
                        }.let { content ->
                            out.write(content.toByteArray(Charsets.UTF_8))
                        }
                    } catch (e: IOException) {
                        warn("generate header file $mHeaderName failed!")
                    }
                }
    }

    private fun StringBuilder.generateSourceContent(headerOnly: Boolean) {
        if (!headerOnly) {
            append(Constants.AUTO_GENERATE_NOTICE)

            append("""
                |#include "$mHeaderName"
                |
                |""".trimMargin())
        }

        if (!mEnv.configurations.errorLoggerFunction.isNullOrBlank()) {
            append("""
                   |
                   |// external logger function passed by ${Configurations.ERROR_LOGGER_FUNCTION}
                   |void ${mEnv.configurations.errorLoggerFunction}(JNIEnv* env, const char* error);
                   |
                   |""".trimMargin())
        }

        append(mNamespaceHelper.beginNamespace())
        append("\n\n")

        buildNativeInitClass(headerOnly)

        append("\n")
        append(mNamespaceHelper.endNamespace())
        append("\n")
    }

    private fun StringBuilder.buildConstantsIdDeclare() {
        mConstants.forEach {
            append("    ${mHelper.getConstexprStatement(it)}\n")
        }
        append('\n')
    }

    private fun StringBuilder.buildConstructorIdDeclare() {
        mConstructors.forEach { r ->
            append("    jmethodID ${getConstructorName(r.index)} = nullptr;\n")
        }
        append('\n')
    }

    private fun StringBuilder.buildMethodIdDeclare() {
        mMethods.forEach { r ->
            append("    jmethodID ${getMethodName(r.method, r.index)} = nullptr;\n")
        }
        append('\n')
    }

    private fun StringBuilder.buildFieldIdDeclare() {
        mFields.forEachIndexed { index, field ->
            if (field.constantValue != null) {
                warn("you are trying to add getter/setter to a compile-time constant "
                        + mClassName + "." + field.simpleName.toString())
            }
            append("    jfieldID ${getFieldName(field, index)} = nullptr;\n")
        }
        append('\n')
    }

    private fun StringBuilder.buildConstructorDefines(useJniHelper: Boolean) {
        mConstructors.forEach { r ->
            val param = makeParam(true, useJniHelper, getJniMethodParam(r.method, useJniHelper))

            val returnType = if (useJniHelper) cppClassName else "jobject"
            append("""
                |    // construct: ${mHelper.getModifiers(r.method)} ${mSimpleClassName}(${mHelper.getJavaMethodParam(r.method)})
                |    static $returnType newInstance${r.resolvedPostFix}(${param}) {
                |        ${methodPrologue(true, useJniHelper)}
                |        return env->NewObject(${getClassState(getClazz())}, ${getClassState(getConstructorName(r.index))}${getJniMethodParamVal(r.method, useJniHelper)});
                |    } 
                |    
                |""".trimMargin())
        }
        append('\n')
    }

    private fun StringBuilder.buildMethodDefines(useJniHelper: Boolean) {
        mMethods.forEach { r ->
            val m = r.method
            val isStatic = m.modifiers.contains(Modifier.STATIC)
            val jniReturnType = mHelper.toJNIType(m.returnType)
            val functionReturnType = m.returnType.toJniTypeForReturn(useJniHelper)
            val staticMod = if (isStatic || !useJniHelper) "static " else ""
            val constMod = if (isStatic || !useJniHelper) "" else "const "

            val jniParam = makeParam(isStatic, useJniHelper, getJniMethodParam(m, useJniHelper))

            if (useJniHelper) {
                append("    // for jni helper\n")
            }

            append("""
                |    // method: ${mHelper.getModifiers(m)} ${m.returnType} ${m.simpleName}(${mHelper.getJavaMethodParam(m)})
                |    ${staticMod}${functionReturnType} ${m.simpleName}${r.resolvedPostFix}(${jniParam}) ${constMod}{
                |        ${methodPrologue(isStatic, useJniHelper)}
                |""".trimMargin())

            if (m.returnType.kind !== TypeKind.VOID) {
                append("        return ")
            } else {
                append("        ")
            }

            if (useJniHelper && needWrapLocalRef(m.returnType)) {
                append(functionReturnType).append("(")
            }

            if (returnTypeNeedCast(jniReturnType)) {
                append("reinterpret_cast<${jniReturnType}>(")
            }

            val static = if (isStatic) "Static" else ""
            val classOrObj = if (isStatic) getClassState(getClazz()) else "thiz"
            append("env->Call${static}${getTypeForJniCall(m.returnType)}Method(${classOrObj}, ${getClassState(getMethodName(m, r.index))}${getJniMethodParamVal(m, useJniHelper)})")
            if (returnTypeNeedCast(jniReturnType)) {
                append(")")
            }
            if (useJniHelper && needWrapLocalRef(m.returnType)) {
                append(")")
            }

            append(";\n")
            append("    }\n\n")
        }
        append('\n')
    }

    private fun StringBuilder.buildFieldDefines(useJniHelper: Boolean) {
        mFields.forEachIndexed { index, f ->
            val isStatic = f.modifiers.contains(Modifier.STATIC)
            val camelCaseName = f.simpleName.toString().capitalize(Locale.ROOT)
            val getterSetters = hasGetterSetter(f)
            val fieldId = getFieldName(f, index)
            val typeForJniCall = getTypeForJniCall(f.asType())


            val static = if (isStatic) "Static" else ""
            val staticMod = if (isStatic || !useJniHelper) "static " else ""
            val constMod = if (isStatic || !useJniHelper) "" else "const "
            val classOrObj = if (isStatic) getClassState(getClazz()) else "thiz"
            val jniEnv = "env"

            var comment = "// field: ${mHelper.getModifiers(f)} ${f.asType()} ${f.simpleName}"
            if (useJniHelper) {
                comment = "    // for jni helper\n    $comment"
            }

            // getter
            if (getterSetters.contains(GetterSetter.GETTER)) {
                val jniReturnType = mHelper.toJNIType(f.asType())
                val functionReturnType = f.asType().toJniTypeForReturn(useJniHelper)
                val param = makeParam(isStatic, useJniHelper, "")
                append("""
                    |    $comment
                    |    ${staticMod}$functionReturnType get${camelCaseName}(${param}) ${constMod}{
                    |       ${methodPrologue(isStatic, useJniHelper)}
                    |       return """.trimMargin())

                if (useJniHelper && needWrapLocalRef(f.asType())) {
                    append(functionReturnType).append("(")
                }

                if (returnTypeNeedCast(jniReturnType)) {
                    append("reinterpret_cast<${jniReturnType}>(")
                }

                append("${jniEnv}->Get${static}${typeForJniCall}Field(${classOrObj}, ${getClassState(fieldId)})")

                if (returnTypeNeedCast(jniReturnType)) {
                    append(")")
                }
                if (useJniHelper && needWrapLocalRef(f.asType())) {
                    append(")")
                }
                append(""";
                    |
                    |    }
                    |
                    |""".trimMargin())
            }

            // setter
            if (getterSetters.contains(GetterSetter.SETTER)) {
                val param = makeParam(isStatic, useJniHelper, "${f.asType().toJniTypeForParam(useJniHelper)} ${f.simpleName}")
                val passedParam = if (useJniHelper && needWrapLocalRef(f.asType())) "${f.simpleName}.get()" else f.simpleName
                append("""
                    |    $comment
                    |    ${staticMod}void set${camelCaseName}(${param}) ${constMod}{
                    |        ${methodPrologue(isStatic, useJniHelper)}
                    |        ${jniEnv}->Set${static}${typeForJniCall}Field(${classOrObj}, ${getClassState(fieldId)}, ${passedParam});
                    |    }
                    |
                    |""".trimMargin())
            }
            append('\n')
        }
    }

    private fun StringBuilder.buildNativeInitClass(headerOnly: Boolean) {
        val prefix = if(headerOnly) "/*static*/ inline" else "/*static*/"
        append("""
            |${prefix} bool $cppClassName::initClazz(JNIEnv* env) {
            |#define JENNY_CHECK_NULL(val)                      \
            |       do {                                        \
            |           if ((val) == nullptr) {                 \
            |""".trimMargin())

        if (!mEnv.configurations.errorLoggerFunction.isNullOrBlank()) {
            append("""
            |               ${mEnv.configurations.errorLoggerFunction}(env, "can't init ${cppClassName}::" #val); \
            |""".trimMargin())
        } else {
            append("""
            |               env->ExceptionDescribe();           \
            |""".trimMargin())
        }

        append("""
            |               return false;                       \
            |           }                                       \
            |       } while(false)
            |
            |""".trimMargin())

        if (mEnv.configurations.threadSafe) {
            append("""
                |    auto& state = getClassInitState();
                |    if (!state.sInited) {
                |        std::lock_guard<std::mutex> lg(state.sInitLock);
                |""".trimMargin())
        }
        append("""
                |        if (!state.sInited) {
                |            auto clazz = env->FindClass(FULL_CLASS_NAME);
                |            JENNY_CHECK_NULL(clazz);
                |            state.sClazz = reinterpret_cast<jclass>(env->NewGlobalRef(clazz));
                |            env->DeleteLocalRef(clazz);
                |            JENNY_CHECK_NULL(state.sClazz);
                |
                |""".trimMargin())

        buildConstructorIdInit()
        buildMethodIdInit()
        buildFieldIdInit()

        append("""
                |            state.sInited = true;
                |        }
                |""".trimMargin())
        if (mEnv.configurations.threadSafe) {
            append("    }\n")
        }

        append("""
            |#undef JENNY_CHECK_NULL
            |   return true;
            |}
            |
            |""".trimMargin())

        if (mEnv.configurations.threadSafe) {
            append("""
                |${prefix} void $cppClassName::releaseClazz(JNIEnv* env) {
                |    auto& state = getClassInitState();
                |    if (state.sInited) {
                |        std::lock_guard<std::mutex> lg(state.sInitLock);
                |        if (state.sInited) {
                |            env->DeleteGlobalRef(state.sClazz);
                |            state.sClazz = nullptr;
                |            state.sInited = false;
                |        }
                |    }
                |}
                |
                |""".trimMargin())
        } else {
            append("""
                |/*static*/ void $cppClassName::releaseClazz(JNIEnv* env) {
                |    auto& state = getClassInitState();
                |    if (state.sInited) {
                |        env->DeleteGlobalRef(state.sClazz);
                |        state.sInited = false;
                |    }
                |}
                |
                |""".trimMargin())
        }
    }

    private fun StringBuilder.buildConstructorIdInit() {
        mConstructors.forEach { r ->
            val c = r.method
            val name = "state.${getConstructorName(r.index)}"
            val signature = mHelper.getBinaryMethodSignature(c)

            append("""
            |            $name = env->GetMethodID(state.sClazz, "<init>", "$signature");
            |            JENNY_CHECK_NULL(${name});
            |
            |""".trimMargin())
        }
        append('\n')
    }

    private fun StringBuilder.buildMethodIdInit() {
        mMethods.forEach { r ->
            val m = r.method
            val name = "state.${getMethodName(m, r.index)}"
            val static = if (m.modifiers.contains(Modifier.STATIC)) "Static" else ""
            val methodName = m.simpleName
            val signature = mHelper.getBinaryMethodSignature(m)

            append("""
            |            $name = env->Get${static}MethodID(state.sClazz, "$methodName", "$signature");
            |            JENNY_CHECK_NULL(${name});
            |
            |""".trimMargin())
        }
        append('\n')
    }

    private fun StringBuilder.buildFieldIdInit() {
        mFields.forEachIndexed { index, f ->
            val name = "state.${getFieldName(f, index)}"
            val static = if (f.modifiers.contains(Modifier.STATIC)) "Static" else ""
            val fieldName = f.simpleName
            val signature = mHelper.getBinaryTypeSignature(f.asType())

            append("""
            |            $name = env->Get${static}FieldID(state.sClazz, "$fieldName", "$signature");
            |            JENNY_CHECK_NULL(${name});
            |
            |""".trimMargin())
        }
        append('\n')
    }

    private fun makeParam(vararg params: String): String =
            params.filter { it.isNotEmpty() }.joinToString(", ")

    private fun makeParam(isStatic: Boolean, useJniHelper: Boolean, jniParam: String): String =
            if (!useJniHelper) {
                if (isStatic) {
                    makeParam("JNIEnv* env", jniParam)
                } else {
                    makeParam("JNIEnv* env", "jobject thiz", jniParam)
                }
            } else {
                jniParam
            }

    private fun methodPrologue(isStatic: Boolean, useJniHelper: Boolean): String =
            if (useJniHelper) {
                if (isStatic) {
                    "::jenny::Env env; assertInited(env.get());"
                } else {
                    "::jenny::Env env; ::jenny::LocalRef<jobject> jennyLocalRef = getThis(false); jobject thiz = jennyLocalRef.get();"
                }
            } else {
                "assertInited(env);"
            }

    private fun shouldGenerateMethod(m: ExecutableElement): Boolean {
        val annotation = m.getAnnotation(NativeMethodProxy::class.java)
        return annotation?.enabled ?: mNativeProxyConfig.allMethods
    }

    private fun shouldGenerateField(f: Element): Boolean {
        return !hasGetterSetter(f).isEmpty()
    }

    private enum class GetterSetter {
        GETTER, SETTER
    }

    private fun hasGetterSetter(field: Element): EnumSet<GetterSetter> {
        var getter = false
        var setter = false

        var auto = mNativeProxyConfig.allFields
        val annotation = field.getAnnotation(NativeFieldProxy::class.java)
        if (annotation != null) {
            auto = false
            getter = annotation.getter
            setter = annotation.setter
        } else {
            if (mConstants.contains(field)) {
                auto = false
                //don't generate
                getter = false
                setter = false
            }
        }

        if (auto) {
            val camelCaseName = field.simpleName.toString().capitalize()
            setter = !mMethodSimpleName.contains("set$camelCaseName")

            val type = mHelper.toJNIType(field.asType())
            getter = !mMethodSimpleName.contains("get$camelCaseName")
            if ("jboolean" == type) {
                getter = getter and !mMethodSimpleName.contains("is$camelCaseName")
            }
        }

        return if (getter && setter) {
            EnumSet.of(GetterSetter.GETTER, GetterSetter.SETTER)
        } else if (getter) {
            EnumSet.of(GetterSetter.GETTER)
        } else if (setter) {
            EnumSet.of(GetterSetter.SETTER)
        } else {
            EnumSet.noneOf(GetterSetter::class.java)
        }
    }


    private fun returnTypeNeedCast(returnType: String): Boolean {
        return when (returnType) {
            "jclass", "jstring", "jarray", "jobjectArray",
            "jbooleanArray", "jbyteArray", "jcharArray",
            "jshortArray", "jintArray", "jlongArray",
            "jfloatArray", "jdoubleArray",
            "jthrowable", "jweak" -> true
            else ->
                // primitive type or jobject or void
                false
        }
    }

    private fun getClassState(what: String): String {
        return "getClassInitState().$what"
    }

    private fun getClazz(): String {
        return "sClazz"
    }

    private fun getConstructorName(index: Int): String {
        return "sConstruct_$index"
    }

    private fun getMethodName(e: ExecutableElement, index: Int): String {
        return "sMethod_" + e.simpleName + "_" + index
    }

    private fun getFieldName(e: Element, index: Int): String {
        return "sField_" + e.simpleName + "_" + index
    }

    private fun findConstructors() {
        mClazz.enclosedElements
                .asSequence()
                .filter { it.kind == ElementKind.CONSTRUCTOR }
                .map { it as ExecutableElement }
                .filter { visibilityMatched(it) && shouldGenerateMethod(it) }
                .toList()
                .let {
                    MethodOverloadResolver(mHelper, this::getJniMethodParamTypes).resolve(it)
                            .let { mConstructors.addAll(it) }
                }
    }

    private fun findMethods() {
        mClazz.enclosedElements
                .asSequence()
                .filter { it.kind == ElementKind.METHOD }
                .map { it as ExecutableElement }
                .filter { visibilityMatched(it) && shouldGenerateMethod(it) }
                .groupBy { it.simpleName.toString() }
                .forEach { (simpleName, methodList) ->
                    mMethodSimpleName.add(simpleName)
                    MethodOverloadResolver(mHelper, this::getJniMethodParamTypes).resolve(methodList).let {
                        mMethods.addAll(it)
                    }
                }
    }

    private fun findConstants() {
        mClazz.enclosedElements
                .asSequence()
                .filter {
                    it.kind.isField && visibilityMatched(it)
                            && it.modifiers.containsAll(listOf(Modifier.STATIC, Modifier.FINAL))
                            && (it as VariableElement).constantValue != null
                }
                .forEach { mConstants.add(it as VariableElement) }
    }

    private fun findFields() {
        mClazz.enclosedElements
                .asSequence()
                .filter { it.kind.isField && shouldGenerateField(it) && visibilityMatched(it) }
                .forEach { mFields.add(it as VariableElement) }
    }

    private fun visibilityMatched(element: Element): Boolean {
        if (mNativeProxyConfig.onlyPublic) {
            return element.modifiers.contains(Modifier.PUBLIC)
        }
        return true
    }

    private fun getJniMethodParamTypes(m: ExecutableElement) = buildString {
        var needComma = false
        if (mHelper.isNestedClass(mClazz)) {
            val enclosingElement = mClazz.enclosingElement
            // nested class has an this$0 in its constructor
            append(mHelper.toJNIType(enclosingElement.asType()))
            needComma = true
        }
        m.parameters.forEach { p ->
            if (needComma) append(", ")
            append(mHelper.toJNIType(p.asType()))
            needComma = true
        }
    }

    // need wrap LocalRef for jnihelper or not
    fun needWrapLocalRef(type: TypeMirror): Boolean {
        return !type.kind.isPrimitive && type !is NoType
    }

    fun TypeMirror.toJniTypeForParam(useJniHelper: Boolean): String {
        val jniType = mHelper.toJNIType(this)
        return if (useJniHelper && needWrapLocalRef(this)) {
            "const ::jenny::LocalRef<$jniType>&"
        } else {
            jniType
        }
    }

    fun TypeMirror.toJniTypeForReturn(useJniHelper: Boolean): String {
        val jniType = mHelper.toJNIType(this)
        return if (useJniHelper && needWrapLocalRef(this)) {
            "::jenny::LocalRef<$jniType>"
        } else {
            jniType
        }
    }

    private fun getJniMethodParam(m: ExecutableElement, useJniHelper: Boolean) = buildString {
        var needComma = false
        if (mHelper.isNestedClass(mClazz)) {
            val enclosingElement = mClazz.enclosingElement
            // nested class has an this$0 in its constructor
            append(enclosingElement.asType().toJniTypeForParam(useJniHelper))
                    .append(" ")
                    .append("enclosingClass")
            needComma = true
        }
        m.parameters.forEach { p ->
            if (needComma) append(", ")
            append(p.asType().toJniTypeForParam(useJniHelper))
                    .append(" ")
                    .append(p.simpleName)
            needComma = true
        }
    }

    private fun getJniMethodParamVal(m: ExecutableElement, useJniHelper: Boolean): String {
        val sb = StringBuilder(64)
        if (mHelper.isNestedClass(mClazz)) {
            // nested class has an `jboject this$0` in its constructor
            sb.append(", ").append("enclosingClass")
            if (useJniHelper) {
                // LocalRef::get
                sb.append(".get()")
            }
        }
        m.parameters.forEach { p ->
            sb.append(", ").append(p.simpleName)
            if (useJniHelper && needWrapLocalRef(p.asType())) {
                // LocalRef::get
                sb.append(".get()")
            }
        }
        return sb.toString()
    }

    private fun getTypeForJniCall(type: TypeMirror): String {
        val result: String
        val k = type.kind
        result = if (k.isPrimitive || k == TypeKind.VOID) {
            k.name.toLowerCase(Locale.US)
        } else {
            "object"
        }
        return result.capitalize()
    }
}
