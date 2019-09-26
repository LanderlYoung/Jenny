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

import com.google.common.collect.ArrayListMultimap
import io.github.landerlyoung.jenny.template.FileTemplate
import java.io.IOException
import java.io.Writer
import java.util.*
import java.util.function.Predicate
import java.util.stream.Stream
import javax.lang.model.element.*
import javax.lang.model.type.TypeKind
import javax.lang.model.type.TypeMirror
import javax.tools.StandardLocation

/**
 * Author: landerlyoung@gmail.com
 * Date:   2016-06-05
 * Time:   00:30
 * Life with Passion, Code with Creativity.
 */
class NativeProxyCodeGenerator(env: Environment, clazz: TypeElement) : AbsCodeGenerator(env, clazz) {
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

    private val mConstructors: LinkedList<ExecutableElement>
    private val mMethods: ArrayListMultimap<String, ExecutableElement>
    private val mFields: ArrayListMultimap<String, Element>
    private val mConsts: MutableSet<String>
    private val mNativeProxyAnnotation: NativeProxy
    private val mHeaderName: String
    private val mSourceName: String

    private var mDummyIndex: Int = 0

    private val cppClassName: String
        get() {
            val fileName = mNativeProxyAnnotation.fileName
            return if (fileName.length > 0) {
                fileName
            } else {
                (if (mNativeProxyAnnotation.simpleName)
                    mSimpleClassName
                else
                    mJNIClassName) + "Proxy"
            }
        }


    init {
        mConstructors = LinkedList()
        mMethods = ArrayListMultimap.create(16, 16)
        mFields = ArrayListMultimap.create(16, 16)
        mConsts = HashSet()

        var annotation: NativeProxy? = clazz.getAnnotation(NativeProxy::class.java)
        if (annotation == null) {
            annotation = AnnotationResolver.getDefaultImplementation(NativeProxy::class.java)
        }
        mNativeProxyAnnotation = annotation

        mHeaderName = "${cppClassName}.h"
        mSourceName = "${cppClassName}.cpp"
    }

    private fun init() {
        findConstructors()
        findMethods()
        findFields()
    }

    override fun doGenerate() {
        init()

        generatorHeader()
//        generateSource()
//
//        writeToFile(mFileName + HEADER_POST_FIX, FileTemplate
//                .withType(FileTemplate.Type.NATIVE_PROXY_SKELETON_HEADER)
//                .add("cpp_class_name", cppClassName)
//                .add("consts", generateConstantsDefinition())
//                .add("full_class_name_const", mSlashClassName)
//                .add("constructors_id_declare", generateConstructorIdDeclare())
//                .add("methods_id_declare", generateMethodIdDeclare())
//                .add("fields_id_declare", generateFieldIdDeclare())
//                .add("constructors_id_init", generateConstructorIdInit())
//                .add("methods_id_init", generateMethodIdInit())
//                .add("fields_id_init", generateFieldIdInit())
//                .add("constructors", generateConstructors())
//                .add("methods", generateMethods())
//                .add("fields_getter_setter", generateFields())
//                .create()
//        )
//
//        writeToFile(mFileName + SOURCE_POST_FIX, FileTemplate
//                .withType(FileTemplate.Type.NATIVE_PROXY_SKELETON_SOURCE)
//                .add("cpp_class_name", cppClassName)
//                .add("static_declare", generateCppStaticDeclare())
//                .create()
//        )
    }

    private fun generatorHeader() {
        val fileObject = mEnv.filer.createResource(StandardLocation.SOURCE_OUTPUT, PKG_NAME, mHeaderName)
        fileObject.openOutputStream().use { out ->
            try {
                log("write native proxy file [" + fileObject.name + "]")
                buildString {
                    append(FileTemplate.AUTO_GENERATE_NOTICE)
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
                        |class $cppClassName {
                        |
                        |public:
                        |    static constexpr auto FULL_CLASS_NAME = "$mSlashClassName";
                        |    
                        |private:
                        |    static jclass sClazz;
                        |
                    """.trimMargin())

                    buildConstantsIdDeclare()
                    buildConstructorIdDeclare()
                    buildMethodIdDeclare()
                    buildFieldIdDeclare()

                    append("private:\n")

                    if (mEnv.configurations.threadSafe) {
                        append("""
                        |    static std::atomic_bool sInited;
                        |    static std::mutex sInitLock;
                        |""".trimMargin())
                    } else {
                        append("    static bool sInited;\n")
                    }

                    append("""
                        |
                        |private:
                        |    JNIEnv *const mJniEnv;
                        |    const jobject mJavaObjectReference;
                        |
                        |public:
                        |
                        |    static bool initClazz(JNIEnv *env);
                        |    
                        |    static void releaseClazz(JNIEnv *env);
                        |
                        |    static void assertInited(JNIEnv *env) {
                        |        assert(initClazz(env));
                        |    }
                        |
                        |    ${cppClassName}(JNIEnv *env, jobject javaObj)
                        |            : mJniEnv(env), mJavaObjectReference(javaObj) {
                        |        assertInited(env);
                        |    }
                        |
                        |    ${cppClassName}(const $cppClassName &from) = default;
                        |    $cppClassName &operator=(const $cppClassName &) = default;
                        |
                        |    // trivial struct, no move needed
                        |    ${cppClassName}(const $cppClassName &&from) = delete;
                        |
                        |    ~${cppClassName}() = default;
                        |    
                        |""".trimMargin())

                    buildConstructorDefines()
                    buildMethodDefines()
                    buildFieldDefines()

                    append("};")

                }.let { content ->
                    out.write(content.toByteArray(Charsets.UTF_8))
                }
            } catch (e: IOException) {
                warn("generate header file $mHeaderName failed!")
            }
        }
    }

    private fun StringBuilder.buildConstantsIdDeclare() {
        mClazz.enclosedElements
                .stream()
                .filter { e -> e.kind == ElementKind.FIELD }
                .map { e -> e as VariableElement }
                .filter { ve -> ve.constantValue != null }
                .forEach { ve ->
                    // if this field is a compile-time constant value it's
                    // value will be returned, otherwise null will be returned.
                    val constValue = ve.constantValue!!

                    mConsts.add(ve.simpleName.toString())
                    val type = mHelper.toNativeType(ve.asType(), true)
                    val name = ve.simpleName
                    val value = HandyHelper.getJNIHeaderConstantValue(constValue)
                    append("    static constexpr const $type $name = ${value};\n")
                }
        append('\n')
    }

    private fun StringBuilder.buildConstructorIdDeclare() {
        mConstructors.forEachIndexed { index, c ->
            append("    static jmethodID ${getConstructorName(c, index)};\n")
        }
        append('\n')
    }

    private fun StringBuilder.buildMethodIdDeclare() {
        mMethods.values().forEachIndexed { index, m ->
            append("    static jmethodID ${getMethodName(m, index)};\n")
        }
        append('\n')

    }

    private fun StringBuilder.buildFieldIdDeclare() {
        mFields.values().forEachIndexed { index, m ->
            val f = m as VariableElement
            if (f.constantValue != null) {
                warn("you are trying to add getter/setter to a compile-time constant "
                        + mClassName + "." + f.simpleName.toString())
            }
            append("    static jfieldId ${getFieldName(m, index)};\n")
        }
        append('\n')
    }

    private fun StringBuilder.buildConstructorDefines() {
        mConstructors.forEachIndexed { index, c ->
            append("""
                |    // construct ${mSimpleClassName}(${mHelper.getJavaMethodParam(c)})
                |    jobject newInstance(${getJniMethodParam(c)}) noexcept {
                |       assertInited(mJniEnv);
                |       auto clazz = mJniEnv->FindClass(FULL_CLASS_NAME);
                |       auto ret = mJniEnv->NewObject(clazz, ${getConstructorName(c, index)}${getJniMethodParamVal(c)});
                |       mJniEnv->DeleteLocalRef(clazz);
                |       return ret;
                |    } 
                |    
                |""".trimMargin())
        }
        append('\n')
    }

    private fun StringBuilder.buildMethodDefines() {
        mMethods.values().forEachIndexed { index, m ->
            val isStatic = m.modifiers.contains(Modifier.STATIC)
            val returnType = mHelper.toJNIType(m.returnType)

            append("""
                |    // method: ${m.returnType} ${m.simpleName}(${mHelper.getJavaMethodParam(m)})
                |    $returnType ${m.simpleName}(${getJniMethodParam(m)}) const {
                |""".trimMargin())

            if (m.returnType.kind !== TypeKind.VOID) {
                append("        return ")
                if (returnTypeNeedCast(returnType)) {
                    append("reinterpret_cast<${returnType}>(")
                }

                val static = if (isStatic) "Static" else ""
                val classOrObj = if (isStatic) "sClazz" else "mJavaObjectReference"
                append("mJniEnv->Call${static}${getTypeForJniCall(m.returnType)}Method(${classOrObj}, ${getMethodName(m, index)}${getJniMethodParamVal(m)})")
                if (returnTypeNeedCast(returnType)) {
                    append(")")
                }
                append(";\n")
            }
            append("    }\n\n")
        }
        append('\n')
    }

    private fun StringBuilder.buildFieldDefines() {
        mFields.values().forEachIndexed { index, f ->
            val isStatic = f.modifiers.contains(Modifier.STATIC)
            val isFinal = f.modifiers.contains(Modifier.FINAL)
            val camelCaseName = camelCase(f.simpleName.toString())
            val returnType = mHelper.toJNIType(f.asType())
            val getterSetters = hasGetterSetter(f)
            val fieldId = getFieldName(f, index)
            val typeForJniCall = getTypeForJniCall(f.asType())
            val jniType = mHelper.toJNIType(f.asType())


            val static = if (isStatic) "Static" else ""
            val classOrObj = if (isStatic) "sClazz" else "mJavaObjectReference"

            val comment = "// field: ${if (isStatic) "static" else ""} ${if (isFinal) "final" else ""} ${f.asType()} ${f.simpleName}"

            if (getterSetters.contains(GetterSetter.GETTER)) {
                append("""
                    |    $comment
                    |    $returnType get${camelCaseName}() const {
                    |       return mJniEnv->Get${static}${typeForJniCall}Field(${classOrObj}, ${fieldId});
                    |    }
                    |""".trimMargin())
            }

            if (getterSetters.contains(GetterSetter.SETTER)) {
                append("""
                    |    $comment
                    |    void set${camelCaseName}(${jniType} ${f.simpleName}) const {
                    |        mJniEnv->Set${static}${typeForJniCall}Field(${classOrObj}, ${fieldId}, ${f.simpleName});
                    |    }
                    |""".trimMargin())
            }
            append('\n')
        }
    }


    private fun fieldsStream(): Stream<Element> {
        return mFields.values()
                .stream()
                .filter { this.shouldGenerateField(it) }
    }

    private fun constructorsStream(): Stream<ExecutableElement> {
        return mConstructors.stream()
                .filter(Predicate<ExecutableElement> { this.shouldGenerateMethod(it) })
    }

    private fun methodsStream(): Stream<ExecutableElement> {
        return mMethods.values()
                .stream()
                .filter(Predicate<ExecutableElement> { this.shouldGenerateMethod(it) })
    }

    private fun shouldGenerateMethod(m: ExecutableElement): Boolean {
        val annotation = m.getAnnotation(NativeMethodProxy::class.java)
        return annotation?.enabled ?: mNativeProxyAnnotation.allMethods
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

        var auto = mNativeProxyAnnotation.allFields
        val annotation = field.getAnnotation(NativeFieldProxy::class.java)
        if (annotation != null) {
            auto = false
            getter = annotation.getter
            setter = annotation.setter
        } else {
            if (mConsts.contains(field.simpleName.toString())) {
                auto = false
                //don't generate
                getter = false
                setter = false
            }
        }

        if (auto) {
            val camelCaseName = camelCase(field.simpleName.toString())
            setter = !mMethods.containsKey("set$camelCaseName")

            val type = mHelper.toJNIType(field.asType())
            getter = !mMethods.containsKey("get$camelCaseName")
            if ("jboolean" == type) {
                getter = getter and !mMethods.containsKey("is$camelCaseName")
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


    private fun generateConstantsDefinition(): String {
        val sb = StringBuilder()
        //if this field is a compile-time constant value it's
        //value will be returned, otherwise null will be returned.
        mClazz.enclosedElements
                .stream()
                .filter { e -> e.kind == ElementKind.FIELD }
                .map { e -> e as VariableElement }
                .filter { ve -> ve.constantValue != null }
                .forEach { ve ->
                    //if this field is a compile-time constant value it's
                    //value will be returned, otherwise null will be returned.
                    val constValue = ve.constantValue

                    mConsts.add(ve.simpleName.toString())
                    sb.append(FileTemplate.withType(FileTemplate.Type.NATIVE_PROXY_CONSTANT)
                            .add("type", mHelper.toNativeType(ve.asType(), true))
                            .add("name", ve.simpleName.toString())
                            .add("value", HandyHelper.getJNIHeaderConstantValue(constValue))
                            .create()
                    )
                }
        return sb.toString()
    }

    private fun generateConstructorIdDeclare(): String {
        mDummyIndex = 0
        val sb = StringBuilder()
        constructorsStream().forEach { c ->
            sb.append(FileTemplate
                    .withType(FileTemplate.Type.NATIVE_PROXY_METHOD_ID_DECLARE)
                    .add("name", getConstructorName(c, mDummyIndex++))
                    .create()
            )
        }
        return sb.toString()
    }

    private fun generateMethodIdDeclare(): String {
        mDummyIndex = 0
        val sb = StringBuilder()
        methodsStream().forEach { m ->
            sb.append(FileTemplate
                    .withType(FileTemplate.Type.NATIVE_PROXY_METHOD_ID_DECLARE)
                    .add("name", getMethodName(m, mDummyIndex++))
                    .create()
            )
        }
        return sb.toString()
    }

    private fun generateFieldIdDeclare(): String {
        mDummyIndex = 0
        val sb = StringBuilder()
        fieldsStream()
                .map { e -> e as VariableElement }
                .forEach { f ->
                    if (f.constantValue != null) {
                        warn("you are trying to add getter/setter to a compile-time constant "
                                + mClassName + "." + f.simpleName.toString())
                    }
                    sb.append(FileTemplate
                            .withType(FileTemplate.Type.NATIVE_PROXY_FIELD_ID_DECLARE)
                            .add("name", getFieldName(f, mDummyIndex++))
                            .create()
                    )
                }

        return sb.toString()
    }

    private fun generateConstructorIdInit(): String {
        mDummyIndex = 0
        val sb = StringBuilder()
        constructorsStream().forEach { c ->
            sb.append(FileTemplate
                    .withType(FileTemplate.Type.NATIVE_PROXY_METHOD_ID_INIT)
                    .add("name", getConstructorName(c, mDummyIndex++))
                    .add("static", "")
                    .add("method_name", "<init>")
                    .add("method_signature", mHelper.getBinaryMethodSignature(c))
                    .create()
            )
        }
        return sb.toString()
    }

    private fun generateMethodIdInit(): String {
        mDummyIndex = 0
        val sb = StringBuilder()
        methodsStream().forEach { m ->
            sb.append(FileTemplate
                    .withType(FileTemplate.Type.NATIVE_PROXY_METHOD_ID_INIT)
                    .add("name", getMethodName(m, mDummyIndex++))
                    .add("static", if (m.modifiers.contains(Modifier.STATIC)) "Static" else "")
                    .add("method_name", m.simpleName.toString())
                    .add("method_signature", mHelper.getBinaryMethodSignature(m))
                    .create()
            )
        }
        return sb.toString()
    }

    private fun generateFieldIdInit(): String {
        mDummyIndex = 0
        val sb = StringBuilder()
        fieldsStream().forEach { f ->
            sb.append(FileTemplate
                    .withType(FileTemplate.Type.NATIVE_PROXY_FIELD_ID_INIT)
                    .add("name", getFieldName(f, mDummyIndex++))
                    .add("static", if (f.modifiers.contains(Modifier.STATIC)) "Static" else "")
                    .add("field_name", f.simpleName.toString())
                    .add("field_signature", mHelper.getBinaryTypeSignature(f.asType()))
                    .create()
            )
        }

        return sb.toString()
    }

    private fun generateConstructors(): String {
        mDummyIndex = 0
        val sb = StringBuilder()
        constructorsStream().forEach { c ->
            sb.append(FileTemplate
                    .withType(FileTemplate.Type.NATIVE_PROXY_CONSTRUCTORS)
                    .add("constructor_method_id", getConstructorName(c, mDummyIndex++))
                    .add("param_declare", getJniMethodParam(c))
                    .add("param_val", getJniMethodParamVal(c))
                    .create()
            )
        }
        return sb.toString()
    }

    private fun generateMethods(): String {
        mDummyIndex = 0
        val sb = StringBuilder()
        methodsStream().forEach { m ->
            val isStatic = m.modifiers.contains(Modifier.STATIC)
            val returnType = mHelper.toJNIType(m.returnType)
            val returnStatement = FileTemplate
                    .withType(FileTemplate.Type.NATIVE_PROXY_METHOD_RETURN)
                    .add("static", if (isStatic) "Static" else "")
                    .add("param_value", getJniMethodParamVal(m))
                    .add("clazz_or_obj", if (isStatic) "sClazz" else "mJavaObjectReference")
                    .add("type", getTypeForJniCall(m.returnType))
                    .create()

            sb.append(FileTemplate
                    .withType(FileTemplate.Type.NATIVE_PROXY_METHODS)
                    .add("name", m.simpleName.toString())
                    .add("method_id", getMethodName(m, mDummyIndex++))
                    .add("_static", if (isStatic) "static " else "")
                    .add("_const", if (isStatic) "" else "const ")
                    .add("return_type", returnType)
                    .add("param_declare", getJniMethodParam(m))
                    .add("return", if (m.returnType.kind != TypeKind.VOID) "return " else "")
                    .add("return_statement", if (!returnTypeNeedCast(returnType))
                        returnStatement
                    else
                        FileTemplate.withType(FileTemplate.Type.REINTERPRET_CAST)
                                .add("type", returnType)
                                .add("expression", returnStatement)
                                .create())
                    .create()
            )
        }
        return sb.toString()
    }

    private fun generateFields(): String {
        mDummyIndex = 0
        val sb = StringBuilder()
        fieldsStream().forEach { f ->
            val isStatic = f.modifiers.contains(Modifier.STATIC)
            val camelCaseName = camelCase(f.simpleName.toString())
            val returnType = mHelper.toJNIType(f.asType())
            val getterSetters = hasGetterSetter(f)

            val r = HashMap<String, String>()
            r["_static"] = if (isStatic) "static " else ""
            r["return_type"] = returnType
            r["camel_case_name"] = camelCaseName
            r["_const"] = if (isStatic) "" else "const "
            r["static"] = if (isStatic) "Static" else ""
            r["_type"] = getTypeForJniCall(f.asType())
            r["clazz_or_obj"] = if (isStatic) "sClazz" else "mJavaObjectReference"
            r["field_id"] = getFieldName(f, mDummyIndex++)
            r["name"] = f.simpleName.toString()
            r["type"] = mHelper.toJNIType(f.asType())

            val returnStatement = FileTemplate.withType(
                    FileTemplate.Type.NATIVE_PROXY_FIELDS_GETTER_RETURN)
                    .create(r)

            sb.append(FileTemplate
                    .withType(FileTemplate.Type.NATIVE_PROXY_FIELDS_GETTER_SETTER)
                    .add("getter", if (!getterSetters.contains(GetterSetter.GETTER))
                        ""
                    else
                        FileTemplate.withType(FileTemplate.Type.NATIVE_PROXY_FIELDS_GETTER)
                                .add("return_statement", if (!returnTypeNeedCast(returnType))
                                    returnStatement
                                else
                                    FileTemplate.withType(FileTemplate.Type.REINTERPRET_CAST)
                                            .add("type", returnType)
                                            .add("expression", returnStatement)
                                            .create())
                                .create(r))
                    .add("setter", if (!getterSetters.contains(GetterSetter.SETTER))
                        ""
                    else
                        FileTemplate.withType(FileTemplate.Type.NATIVE_PROXY_FIELDS_SETTER)
                                .create(r))
                    .create()
            )
        }

        return sb.toString()
    }

    private fun returnTypeNeedCast(returnType: String): Boolean {
        when (returnType) {
            "jclass", "jstring", "jarray", "jobjectArray", "jbooleanArray", "jbyteArray", "jcharArray", "jshortArray", "jintArray", "jlongArray", "jfloatArray", "jdoubleArray", "jthrowable", "jweak" -> return true
            else ->
                //primitive type or jobject or void
                return false
        }
    }

    private fun generateCppStaticDeclare(): String {
        val sb = StringBuilder(2048)

        mDummyIndex = 0
        constructorsStream().forEach { c ->
            sb.append(FileTemplate
                    .withType(FileTemplate.Type.NATIVE_PROXY_CPP_STATIC_INIT)
                    .add("type", "jmethodID")
                    .add("cpp_class_name", cppClassName)
                    .add("name", getConstructorName(c, mDummyIndex++))
                    .create()
            )
        }

        mDummyIndex = 0
        methodsStream().forEach { m ->
            sb.append(FileTemplate
                    .withType(FileTemplate.Type.NATIVE_PROXY_CPP_STATIC_INIT)
                    .add("type", "jmethodID")
                    .add("cpp_class_name", cppClassName)
                    .add("name", getMethodName(m, mDummyIndex++))
                    .create())
        }

        mDummyIndex = 0
        fieldsStream().forEach { f ->
            sb.append(FileTemplate
                    .withType(FileTemplate.Type.NATIVE_PROXY_CPP_STATIC_INIT)
                    .add("type", "jfieldID")
                    .add("cpp_class_name", cppClassName)
                    .add("name", getFieldName(f, mDummyIndex++))
                    .create())
        }
        return sb.toString()
    }

    private fun getConstructorName(e: ExecutableElement, index: Int): String {
        return "sConstruct_$index"
    }

    private fun getMethodName(e: ExecutableElement, index: Int): String {
        return "sMethod_" + e.simpleName + "_" + index
    }

    private fun getFieldName(e: Element, index: Int): String {
        return "sField_" + e.simpleName + "_" + index
    }

    private fun writeToFile(fileName: String, content: String) {
        var w: Writer? = null
        try {
            val fileObject = mEnv.filer.createResource(StandardLocation.SOURCE_OUTPUT, PKG_NAME, fileName)
            log("write native proxy file [" + fileObject.name + "]")
            w = fileObject.openWriter()
            w!!.write(content)
            w.close()
        } catch (e: IOException) {
//            warn("generate header file $mFileName failed!")
        } finally {
            IOUtils.closeSilently(w)
        }
    }

    private fun findConstructors() {
        mClazz.enclosedElements
                .stream()
                .filter { e -> e.kind == ElementKind.CONSTRUCTOR }
                .map { e -> e as ExecutableElement }
                .filter { e -> shouldGenerateMethod(e) }
                .forEach { mConstructors.add(it) }
    }

    private fun findMethods() {
        mClazz.enclosedElements
                .stream()
                .filter { e -> e.kind == ElementKind.METHOD }
                .map { e -> e as ExecutableElement }
                .filter { e -> shouldGenerateMethod(e) }
                .forEach { e ->
                    mMethods.put(
                            e.simpleName.toString(),
                            e
                    )
                }
    }

    private fun findFields() {
        mClazz.enclosedElements
                .stream()
                .filter { e -> e.kind == ElementKind.FIELD }
                .filter { e -> shouldGenerateField(e) }
                .forEach { e ->
                    mFields.put(
                            e.simpleName.toString(),
                            e)
                }
    }

    private fun getJniMethodParam(m: ExecutableElement) = buildString {
        var needComma = false
        if (mHelper.isNestedClass(mClazz)) {
            val enclosingElement = mClazz.enclosingElement
            //nested class has an this$0 in its constructor
            append(mHelper.toJNIType(enclosingElement.asType()))
                    .append(" ")
                    .append("enclosingClass")
            needComma = true
        }
        m.parameters.forEach { p ->
            if (needComma) append(",")
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
        return camelCase(result)
    }

    private fun camelCase(s: String): String {
        return s.capitalize()
    }
}
