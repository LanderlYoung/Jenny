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

import javax.lang.model.element.ElementKind
import javax.lang.model.element.TypeElement
import javax.tools.Diagnostic

/**
 * Author: landerlyoung@gmail.com
 * Date:   2016-06-05
 * Time:   00:42
 * Life with Passion, Code with Creativity.
 */
abstract class AbsCodeGenerator(protected val mEnv: Environment, protected val mClazz: TypeElement) {
    protected val mHelper: HandyHelper

    /** like com.example_package.SomeClass$InnerClass  */
    protected val mClassName: String

    /**
     * like com_example_1package_SomeClass_InnerClass
     * NestedClass
     * com.young.jennysampleapp.ComputeIntensiveClass$NestedNativeClass
     * com_young_jennysampleapp_ComputeIntensiveClass_00024NestedNativeClass
     */
    protected val mJNIClassName: String

    /** like com/example_package/SomeClass$InnerClass  */
    protected val mSlashClassName: String

    /** like String for java.lang.String class  */
    protected val mSimpleClassName: String

    init {

        if (mClazz.kind != ElementKind.CLASS
                && mClazz.kind != ElementKind.INTERFACE
                && mClazz.kind != ElementKind.ENUM
                && mClazz.kind != ElementKind.ANNOTATION_TYPE) {
            error("type element $mClazz is not class type")
        }
        mHelper = HandyHelper(mEnv)

        mClassName = mHelper.getClassName(mClazz)
        mJNIClassName = mHelper.toJNIClassName(mClassName)
        mSlashClassName = mHelper.getSlashClassName(mClassName)
        mSimpleClassName = mHelper.getSimpleName(mClazz)
    }

    abstract fun doGenerate()

    fun log(msg: String) {
        mEnv.messager.printMessage(Diagnostic.Kind.NOTE, LOG_PREFIX + msg)
    }

    fun warn(msg: String) {
        mEnv.messager.printMessage(Diagnostic.Kind.WARNING, LOG_PREFIX + msg)
    }

    fun error(msg: String) {
        mEnv.messager.printMessage(Diagnostic.Kind.ERROR, LOG_PREFIX + msg)
    }

    companion object {
        private const val LOG_PREFIX = "Jenny | "

        const val PKG_NAME = "jenny"
    }
}
