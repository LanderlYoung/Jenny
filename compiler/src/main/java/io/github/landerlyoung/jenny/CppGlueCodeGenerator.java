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
package io.github.landerlyoung.jenny;

import java.io.IOException;
import java.io.Writer;
import java.util.LinkedList;
import java.util.List;

import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.tools.FileObject;
import javax.tools.StandardLocation;

import io.github.landerlyoung.jenny.template.FileTemplate;

/**
 * Author: landerlyoung@gmail.com
 * Date:   2014-12-17
 * Time:   16:03
 * Life with passion. Code with creativity!
 */
public class CppGlueCodeGenerator extends AbsCodeGenerator {
    //header file name
    private String mHeaderName;
    //source file Name
    private String mSourceName;
    private List<Element> mMethods;
    private final NativeClass mNativeClassAnnotation;

    //DONE HandyHelper.toJNIType throwable
    //DONE Use NativeClass to mark generate, NativeCode to add implements
    //DELETE different constant value for different arch
    //DONE fix get package name
    //DONE support for inner class
    //XXXX support for pure c code
    //DONE file output
    //GOING use file template

    public CppGlueCodeGenerator(Environment env, TypeElement clazz) {
        super(env, clazz);
        mMethods = new LinkedList<>();
        NativeClass annotation = clazz.getAnnotation(NativeClass.class);
        if (annotation == null) {
            annotation = AnnotationResolver.INSTANCE.getDefaultImplementation(NativeClass.class);
        }
        mNativeClassAnnotation = annotation;
    }

    @Override
    public void doGenerate() {
        if (init() && !mMethods.isEmpty()) {
            generateHeader();
            generateSource();
        }
    }

    private boolean init() {
        if (!getMClazz().getKind().equals(ElementKind.CLASS)) return false;

        mHeaderName = getCppClassName() + ".h";
        mSourceName = getCppClassName() + ".cpp";
        log("jenny begin generate glue code for class [" + getMClassName() + "]");
        log("header : [" + mHeaderName + "]");
        log("source : [" + mSourceName + "]");

        findNativeMethods();

        return true;
    }

    private String getCppClassName() {
        String fileName = mNativeClassAnnotation.fileName();
        if (fileName.length() > 0) {
            return fileName;
        } else {
            return mNativeClassAnnotation.simpleName() ? getMSimpleClassName() : getMJNIClassName();
        }
    }

    private void findNativeMethods() {
        getMClazz().getEnclosedElements()
              .stream()
              .filter(e -> e.getKind() == ElementKind.METHOD)
              .forEach(e -> {
                  if (e.getModifiers().contains(Modifier.NATIVE)) {
                      mMethods.add(e);
                  } else if (e.getAnnotation(NativeCode.class) != null) {
                      error("Annotation @" + NativeCode.class.getSimpleName()
                              + " should only be applied to NATIVE method!");
                  }
              });
    }

    public void generateHeader() {
        Writer w = null;
        try {
            FileObject fileObject = getMEnv().getFiler().createResource(StandardLocation.SOURCE_OUTPUT, AbsCodeGenerator.PKG_NAME, mHeaderName);
            log("write header file [" + fileObject.getName() + "]");
            w = fileObject.openWriter();


            w.write(FileTemplate.withType(
                    mNativeClassAnnotation.dynamicRegisterJniMethods()
                            ? FileTemplate.Type.JNI_HEADER_SKELETON_NS
                            : FileTemplate.Type.JNI_HEADER_SKELETON)
                                .add("namespace", getCppClassName())
                                .add("full_slash_class_name", getMSlashClassName())
                                .add("full_java_class_name", getMClassName())
                                .add("consts", generateConstantsDefinition())
                                .add("class_prefix", !getMJNIClassName().isEmpty() ? getMJNIClassName() + "_" : "")
                                .add("methods", generateFunctions(false))
                                //ns version only
                                .add("registers", mNativeClassAnnotation.dynamicRegisterJniMethods()
                                        ? FileTemplate.withType(FileTemplate.Type.REGISTER_NATIVE_FUNCTIONS_NS).create()
                                        : "")
                                .create()
            );

            w.close();
        } catch (IOException e) {
            warn("generate header file " + mHeaderName + " failed!");
        } finally {
            IOUtils.closeSilently(w);
        }
    }

    public void generateSource() {
        Writer w = null;
        try {
            FileObject fileObject = getMEnv().getFiler().createResource(StandardLocation.SOURCE_OUTPUT, AbsCodeGenerator.PKG_NAME, mSourceName);
            log("write source file [" + fileObject.getName() + "]");
            w = fileObject.openWriter();

            w.write(FileTemplate.withType(
                    mNativeClassAnnotation.dynamicRegisterJniMethods()
                            ? FileTemplate.Type.NATIVE_CPP_SKELETON_NS
                            : FileTemplate.Type.NATIVE_CPP_SKELETON)
                                .add("header", mHeaderName)
                                .add("namespace", getCppClassName())
                                .add("android_log_marcos", mNativeClassAnnotation.androidLog()
                                        ? FileTemplate.withType(FileTemplate.Type.ANDROID_LOG_MARCOS).create()
                                        : "")
                                .add("full_java_class_name", getMClassName())
                                .add("full_slash_class_name", getMSlashClassName())
                                .add("full_native_class_name", getMJNIClassName())
                                .add("simple_class_name", getMSimpleClassName())
                                .add("methods", generateFunctions(true))
                                .add("dynamic_jni_register", mNativeClassAnnotation.dynamicRegisterJniMethods()
                                        ? FileTemplate.withType(FileTemplate.Type.CPP_DYNAMIC_JNI_REGISTER_NS)
                                                      .add("jni_method_struct", generateJniNativeMethodStruct())
                                                      .add("slash_class_name", getMSlashClassName())
                                                      .create()
                                        : "")
                                .add("jni_onload_impl",
                                        mNativeClassAnnotation.dynamicRegisterJniMethods()
                                                ? FileTemplate.withType(
                                                FileTemplate.Type.JNI_ONLOAD_IMPL_NS)
                                                              .add("namespace", getCppClassName())
                                                              .create()
                                                : "")
                                .create()
            );
        } catch (IOException e) {
            warn("generate source file " + mSourceName + " failed");
        } finally {
            IOUtils.closeSilently(w);
        }
    }

    private String generateConstantsDefinition() {
        StringBuilder sb = new StringBuilder();
        //if this field is a compile-time constant value it's
        //value will be returned, otherwise null will be returned.
        final String constant_prefix = (!mNativeClassAnnotation.dynamicRegisterJniMethods() && !getMJNIClassName().isEmpty() ? getMJNIClassName() + "_" : "");
        getMClazz().getEnclosedElements()
              .stream()
              .filter(e -> e.getKind() == ElementKind.FIELD)
              .map(e -> (VariableElement) e)
              .filter(ve -> ve.getConstantValue() != null)
              .forEach(ve -> {
                  //if this field is a compile-time constant value it's
                  //value will be returned, otherwise null will be returned.
                  Object constValue = ve.getConstantValue();

                  String nativeType = getMHelper().toNativeType(ve.asType(), true);

                  sb.append(FileTemplate.withType(FileTemplate.Type.CONSTANT_TEMPLATE)
                                        .add("type", nativeType)
                                        .add("_const", nativeType.contains("*") ? "const " : "")
                                        .add("name", constant_prefix + ve.getSimpleName().toString())
                                        .add("full_class_name", getMSlashClassName())
                                        .add("value", HandyHelper.getJNIHeaderConstantValue(constValue))
                                        .create()
                  );
              });
        return sb.toString();
    }

    private String generateJniNativeMethodStruct() {
        StringBuilder sb = new StringBuilder();
        int methodLen = mMethods.size();
        for (int i = 0; i < methodLen; i++) {
            ExecutableElement m = (ExecutableElement) mMethods.get(i);
            sb.append(FileTemplate.withType(FileTemplate.Type.NATIVE_JNI_NATIVE_METHOD_STRUCT)
                                  .add("method_name", m.getSimpleName().toString())
                                  .add("signature", getMHelper().getBinaryMethodSignature(m))
                                  .add("comma", i != methodLen - 1 ? "," : "")
                                  .create()
            );
        }
        return sb.toString();
    }

    private String generateFunctions(boolean isSource) {
        StringBuilder sb = new StringBuilder();
        for (Element m : mMethods) {
            ExecutableElement e = (ExecutableElement) m;
            String result =
                    FileTemplate.withType(FileTemplate.Type.NATIVE_METHOD_DECLARE_TEMPLATE)
                                .add("full_java_class_name", getMJNIClassName())
                                .add("modifiers", getMHelper().getMethodModifiers(e))
                                .add("export_", mNativeClassAnnotation.dynamicRegisterJniMethods() ? "" : "JNIEXPORT ")
                                .add("java_return_type", e.getReturnType().toString())
                                .add("note_method_name", e.getSimpleName().toString())
                                .add("method_name", getMethodName(e))
                                .add("java_parameters", getMHelper().getJavaMethodParam(e))
                                .add("jni_return_type", getMHelper().toJNIType(e.getReturnType()))
                                .add("method_signature", getMHelper().getMethodSignature(e))
                                .add("native_parameters", getMHelper().getNativeMethodParam(e))
                                .add("end", isSource ? "" : ";\n\n")
                                .create();
            if (isSource) {
                result = generateFunctionWithReturnStatement(result, e);
            }
            sb.append(result).append("\n");
        }
        if (!isSource && sb.length() != 0 && !mNativeClassAnnotation.dynamicRegisterJniMethods()) {
            sb.insert(0, FileTemplate.withType(FileTemplate.Type.CPP_EXPORT_MARCO_BEGIN).create());
            sb.append(FileTemplate.withType(FileTemplate.Type.CPP_EXPORT_MARCO_END).create());
        }
        return sb.toString();
    }

    private String getMethodName(ExecutableElement m) {
        String simpleName = m.getSimpleName().toString();
        if (mNativeClassAnnotation.dynamicRegisterJniMethods()) {
            return simpleName;
        } else {
            return "Java_" + getMJNIClassName() + "_" + simpleName;
        }
    }

    private String generateFunctionWithReturnStatement(String declare, ExecutableElement m) {
        FileTemplate template = FileTemplate.withType(FileTemplate.Type.NATIVE_METHOD_TEMPLATE)
                                            .add("native_method_declare_template", declare);

        StringBuilder returnStatement = new StringBuilder();
        NativeCode a = m.getAnnotation(NativeCode.class);
        if (a != null) {
            for (String s : a.value()) {
                returnStatement.append("    ")
                               .append(s)
                               .append("\n");
            }
            if (returnStatement.length() > 0) {
                returnStatement.replace(returnStatement.length() - 1, returnStatement.length(), "");
            }
        } else {
            returnStatement.append(getMHelper().getReturnStatement(m));
        }
        return template.add("default_return_statement", returnStatement.toString())
                       .create();
    }
}
