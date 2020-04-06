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
import javax.lang.model.type.TypeKind
import javax.lang.model.type.TypeMirror
import javax.tools.StandardLocation
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

    override fun doGenerate() {
        init()

        generatorHeader()
        generateSource()
    }

    private fun generatorHeader() {
        val fileObject = mEnv.filer.createResource(StandardLocation.SOURCE_OUTPUT, Constants.JENNY_GEN_DIR_PROXY, mHeaderName)
        fileObject.openOutputStream().use { out ->
            try {
                log("write native proxy file [" + fileObject.name + "]")
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
                        |private:
                        |
                    """.trimMargin())

                    if (mEnv.configurations.threadSafe) {
                        append("""
                        |    // thread safe init
                        |    static std::atomic_bool sInited;
                        |    static std::mutex sInitLock;
                        |""".trimMargin())
                    } else {
                        append("    static bool sInited;\n")
                    }

                    append("""
                        |
                        |    JNIEnv* mJniEnv;
                        |    jobject mJavaObjectReference;
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
                        |    ${cppClassName}(): ${cppClassName}(nullptr, nullptr) {}
                        |    
                        |    ${cppClassName}(JNIEnv* env, jobject javaObj)
                        |            : mJniEnv(env), mJavaObjectReference(javaObj) {
                        |        if (env) { assertInited(env); }
                        |    }
                        |
                        |    ${cppClassName}(const $cppClassName& from) = default;
                        |    $cppClassName &operator=(const $cppClassName &) = default;
                        |
                        |    ${cppClassName}($cppClassName&& from) noexcept
                        |           : mJniEnv(from.mJniEnv), mJavaObjectReference(from.mJavaObjectReference) {
                        |        from.mJavaObjectReference = nullptr;
                        |    }
                        |    
                        |    ${cppClassName}& operator=($cppClassName&& from) noexcept {
                        |       mJniEnv = from.mJniEnv;
                        |       std::swap(mJavaObjectReference, from.mJavaObjectReference);
                        |       return *this;
                        |   }
                        |
                        |    ~${cppClassName}() = default;
                        |    
                        |    // helper method to get underlay jobject reference
                        |    jobject operator*() const {
                        |       return mJavaObjectReference;
                        |    }
                        |    
                        |    // helper method to check underlay jobject reference is not nullptr
                        |    operator bool() const {
                        |       return mJavaObjectReference;
                        |    }
                        |    
                        |    // helper method to delete JNI local ref.
                        |    // use only when you really understand JNIEnv::DeleteLocalRef.
                        |    void deleteLocalRef() {
                        |       if (mJavaObjectReference) {
                        |           mJniEnv->DeleteLocalRef(mJavaObjectReference);
                        |           mJavaObjectReference = nullptr;
                        |       }
                        |    }
                        |    
                        |    // === java methods below ===
                        |    
                        |""".trimMargin())

                    buildConstructorDefines()
                    buildMethodDefines()
                    buildFieldDefines()

                    append("""
                        |
                        |private:
                        |    static jclass sClazz;
                        |
                    """.trimMargin())

                    buildConstructorIdDeclare()
                    buildMethodIdDeclare()
                    buildFieldIdDeclare()

                    append("};\n")
                    append(mNamespaceHelper.endNamespace())
                    append("\n")

                }.let { content ->
                    out.write(content.toByteArray(Charsets.UTF_8))
                }
            } catch (e: IOException) {
                warn("generate header file $mHeaderName failed!")
            }
        }
    }

    private fun generateSource() {
        val fileObject = mEnv.filer.createResource(StandardLocation.SOURCE_OUTPUT, Constants.JENNY_GEN_DIR_PROXY, mSourceName)
        fileObject.openOutputStream().use { out ->
            try {
                log("write native proxy file [" + fileObject.name + "]")
                buildString {
                    append(Constants.AUTO_GENERATE_NOTICE)

                    append("""
                        |#include "$mHeaderName"
                        |
                        |""".trimMargin())

                    if (!mEnv.configurations.errorLoggerFunction.isNullOrBlank()) {
                        append("""
                           |
                           |// external logger function passed by ${Configurations.ERROR_LOGGER_FUNCTION}
                           |void ${mEnv.configurations.errorLoggerFunction}(JNIEnv* env, const char* error);
                           |
                           |""".trimMargin())
                    }

                    append("""
                        |
                        |${mNamespaceHelper.beginNamespace()}
                        |
                        |jclass ${cppClassName}::sClazz = nullptr;
                        |
                        |""".trimMargin())

                    if (mEnv.configurations.threadSafe) {
                        append("""
                            |// thread safe init
                            |std::mutex $cppClassName::sInitLock;
                            |std::atomic_bool $cppClassName::sInited;
                            |
                            |""".trimMargin())
                    }

                    buildNativeInitClass()

                    mConstructors.forEach { r ->
                        append("jmethodID ${cppClassName}::${getConstructorName(r.index)};\n")
                    }
                    append("\n")
                    mMethods.forEach { r ->
                        append("jmethodID ${cppClassName}::${getMethodName(r.method, r.index)};\n")
                    }
                    append("\n")
                    mFields.forEachIndexed { index, f ->
                        append("jfieldID ${cppClassName}::${getFieldName(f, index)};\n")
                    }
                    append("\n")
                    append(mNamespaceHelper.endNamespace())
                    append("\n")
                }.let { content ->
                    out.write(content.toByteArray(Charsets.UTF_8))
                }
            } catch (e: IOException) {
                warn("generate header file $mHeaderName failed!")
            }
        }
    }

    private fun StringBuilder.buildConstantsIdDeclare() {
        mConstants.forEach {
            append("    ${mHelper.getConstexprStatement(it)}\n")
        }
        append('\n')
    }

    private fun StringBuilder.buildConstructorIdDeclare() {
        mConstructors.forEach { r ->
            append("    static jmethodID ${getConstructorName(r.index)};\n")
        }
        append('\n')
    }

    private fun StringBuilder.buildMethodIdDeclare() {
        mMethods.forEach { r ->
            append("    static jmethodID ${getMethodName(r.method, r.index)};\n")
        }
        append('\n')
    }

    private fun StringBuilder.buildFieldIdDeclare() {
        mFields.forEachIndexed { index, field ->
            if (field.constantValue != null) {
                warn("you are trying to add getter/setter to a compile-time constant "
                        + mClassName + "." + field.simpleName.toString())
            }
            append("    static jfieldID ${getFieldName(field, index)};\n")
        }
        append('\n')
    }

    private fun StringBuilder.buildConstructorDefines() {
        mConstructors.forEach { r ->
            var param = getJniMethodParam(r.method)
            if (param.isNotEmpty()) {
                param = ", $param"
            }
            append("""
                |    // construct: ${mHelper.getModifiers(r.method)} ${mSimpleClassName}(${mHelper.getJavaMethodParam(r.method)})
                |    static $cppClassName newInstance${r.resolvedPostFix}(JNIEnv* env${param}) noexcept {
                |       assertInited(env);
                |       return ${cppClassName}(env, env->NewObject(sClazz, ${getConstructorName(r.index)}${getJniMethodParamVal(r.method)}));
                |    } 
                |    
                |""".trimMargin())
        }
        append('\n')
    }

    private fun StringBuilder.buildMethodDefines() {
        mMethods.forEach { r ->
            val m = r.method
            val isStatic = m.modifiers.contains(Modifier.STATIC)
            val returnType = mHelper.toJNIType(m.returnType)

            var jniParam = getJniMethodParam(m)
            if (isStatic) {
                jniParam = if (jniParam.isNotEmpty()) {
                    "JNIEnv* env, $jniParam"
                } else {
                    "JNIEnv* env"
                }
            }

            val staticMethod = if (isStatic) "static " else ""
            val env = if (isStatic) "env" else "mJniEnv"
            val constMod = if (isStatic) "" else "const "

            append("""
                |    // method: ${mHelper.getModifiers(m)} ${m.returnType} ${m.simpleName}(${mHelper.getJavaMethodParam(m)})
                |    ${staticMethod}$returnType ${m.simpleName}${r.resolvedPostFix}(${jniParam}) ${constMod}{
                |""".trimMargin())
            if (isStatic) {
                append("        assertInited(env);\n")
            }

            if (m.returnType.kind !== TypeKind.VOID) {
                append("        return ")
            } else {
                append("        ")
            }
            if (returnTypeNeedCast(returnType)) {
                append("reinterpret_cast<${returnType}>(")
            }

            val static = if (isStatic) "Static" else ""
            val classOrObj = if (isStatic) "sClazz" else "mJavaObjectReference"
            append("${env}->Call${static}${getTypeForJniCall(m.returnType)}Method(${classOrObj}, ${getMethodName(m, r.index)}${getJniMethodParamVal(m)})")
            if (returnTypeNeedCast(returnType)) {
                append(")")
            }
            append(";\n")
            append("    }\n\n")
        }
        append('\n')
    }

    private fun StringBuilder.buildFieldDefines() {
        mFields.forEachIndexed { index, f ->
            val isStatic = f.modifiers.contains(Modifier.STATIC)
            val camelCaseName = f.simpleName.toString().capitalize()
            val returnType = mHelper.toJNIType(f.asType())
            val getterSetters = hasGetterSetter(f)
            val fieldId = getFieldName(f, index)
            val typeForJniCall = getTypeForJniCall(f.asType())
            val jniType = mHelper.toJNIType(f.asType())


            val static = if (isStatic) "Static" else ""
            val staticMod = if (isStatic) "static " else ""
            val classOrObj = if (isStatic) "sClazz" else "mJavaObjectReference"
            val assertInit = if (isStatic) "assertInited(env);" else ""
            val jniEnv = if (isStatic) "env" else "mJniEnv"
            val const = if (isStatic)  "" else "const "

            val comment = "// field: ${mHelper.getModifiers(f)} ${f.asType()} ${f.simpleName}"

            if (getterSetters.contains(GetterSetter.GETTER)) {
                val env = if (isStatic) "JNIEnv* env" else ""
                append("""
                    |    $comment
                    |    ${staticMod}$returnType get${camelCaseName}(${env}) ${const}{
                    |       $assertInit
                    |       return """.trimMargin())

                if (returnTypeNeedCast(returnType)) {
                    append("reinterpret_cast<${returnType}>(")
                }

                append("${jniEnv}->Get${static}${typeForJniCall}Field(${classOrObj}, $fieldId)")

                if (returnTypeNeedCast(returnType)) {
                    append(")")
                }

                append(""";
                    |
                    |    }
                    |
                    |""".trimMargin())
            }

            if (getterSetters.contains(GetterSetter.SETTER)) {
                val param = "$jniType ${f.simpleName}".let {
                    if (isStatic) {
                        "JNIEnv* env, $it"
                    } else
                        it
                }
                append("""
                    |    $comment
                    |    ${staticMod}void set${camelCaseName}(${param}) ${const}{
                    |        $assertInit
                    |        ${jniEnv}->Set${static}${typeForJniCall}Field(${classOrObj}, ${fieldId}, ${f.simpleName});
                    |    }
                    |
                    |""".trimMargin())
            }
            append('\n')
        }
    }

    private fun StringBuilder.buildNativeInitClass() {
        append("""
            |/*static*/ bool $cppClassName::initClazz(JNIEnv* env) {
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
                |    if (!sInited) {
                |        std::lock_guard<std::mutex> lg(sInitLock);
                |""".trimMargin())
        }
        append("""
                |        if (!sInited) {
                |            auto clazz = env->FindClass(FULL_CLASS_NAME);
                |            JENNY_CHECK_NULL(clazz);
                |            sClazz = reinterpret_cast<jclass>(env->NewGlobalRef(clazz));
                |            env->DeleteLocalRef(clazz);
                |            JENNY_CHECK_NULL(sClazz);
                |
                |""".trimMargin())

        buildConstructorIdInit()
        buildMethodIdInit()
        buildFieldIdInit()

        append("""
                |            sInited = true;
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
                |/*static*/ void $cppClassName::releaseClazz(JNIEnv* env) {
                |    if (sInited) {
                |        std::lock_guard<std::mutex> lg(sInitLock);
                |        if (sInited) {
                |            env->DeleteGlobalRef(sClazz);
                |            sClazz = nullptr;
                |            sInited = false;
                |        }
                |    }
                |}
                |
                |""".trimMargin())
        } else {
            append("""
                |/*static*/ void $cppClassName::releaseClazz(JNIEnv* env) {
                |    if (sInited) {
                |        env->DeleteGlobalRef(sClazz);
                |        sInited = false;
                |    }
                |}
                |
                |""".trimMargin())
        }
    }

    private fun StringBuilder.buildConstructorIdInit() {
        mConstructors.forEach { r ->
            val c = r.method
            val name = getConstructorName(r.index)
            val signature = mHelper.getBinaryMethodSignature(c)

            append("""
            |            $name = env->GetMethodID(sClazz, "<init>", "$signature");
            |            JENNY_CHECK_NULL(${name});
            |
            |""".trimMargin())
        }
        append('\n')
    }

    private fun StringBuilder.buildMethodIdInit() {
        mMethods.forEach { r ->
            val m = r.method
            val name = getMethodName(m, r.index)
            val static = if (m.modifiers.contains(Modifier.STATIC)) "Static" else ""
            val methodName = m.simpleName
            val signature = mHelper.getBinaryMethodSignature(m)

            append("""
            |            $name = env->Get${static}MethodID(sClazz, "$methodName", "$signature");
            |            JENNY_CHECK_NULL(${name});
            |
            |""".trimMargin())
        }
        append('\n')
    }

    private fun StringBuilder.buildFieldIdInit() {
        mFields.forEachIndexed { index, f ->
            val name = getFieldName(f, index)
            val static = if (f.modifiers.contains(Modifier.STATIC)) "Static" else ""
            val fieldName = f.simpleName
            val signature = mHelper.getBinaryTypeSignature(f.asType())

            append("""
            |            $name = env->Get${static}FieldID(sClazz, "$fieldName", "$signature");
            |            JENNY_CHECK_NULL(${name});
            |
            |""".trimMargin())
        }
        append('\n')
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
            "jclass", "jstring", "jarray", "jobjectArray", "jbooleanArray", "jbyteArray", "jcharArray", "jshortArray", "jintArray", "jlongArray", "jfloatArray", "jdoubleArray", "jthrowable", "jweak" -> true
            else ->
                //primitive type or jobject or void
                false
        }
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
                    it.kind.isField && (it as VariableElement).constantValue != null && visibilityMatched(it)
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

    private fun getJniMethodParam(m: ExecutableElement) = buildString {
        var needComma = false
        if (mHelper.isNestedClass(mClazz)) {
            val enclosingElement = mClazz.enclosingElement
            // nested class has an this$0 in its constructor
            append(mHelper.toJNIType(enclosingElement.asType()))
                    .append(" ")
                    .append("enclosingClass")
            needComma = true
        }
        m.parameters.forEach { p ->
            if (needComma) append(", ")
            append(mHelper.toJNIType(p.asType()))
                    .append(" ")
                    .append(p.simpleName)
            needComma = true
        }
    }

    private fun getJniMethodParamVal(m: ExecutableElement): String {
        val sb = StringBuilder(64)
        if (mHelper.isNestedClass(mClazz)) {
            //nested class has an this$0 in its constructor
            sb.append(", ")
                    .append("enclosingClass")
        }
        m.parameters.forEach { p ->
            sb.append(", ")
                    .append(p.simpleName)
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
