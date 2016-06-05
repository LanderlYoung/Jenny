package com.young.util.jni.generator;

import com.young.jenny.annotation.NativeCode;
import com.young.util.jni.generator.template.FileTemplate;

import java.io.Closeable;
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
    }

    public void doGenerate() {
        if (init() && !mMethods.isEmpty()) {
            generateHeader();
            generateSource();
        }
    }

    private boolean init() {
        if (!mClazz.getKind().equals(ElementKind.CLASS)) return false;

        mClassName = mHelper.getClassName(mClazz);
        mJNIClassName = mHelper.toJNIClassName(mClassName);
        mHeaderName = mJNIClassName + ".h";
        mSourceName = mJNIClassName + ".cpp";
        mSlashClassName = mHelper.getSlashClassName(mClassName);
        log("jenny begin generate glue code for class [" + mClassName + "]");
        log("header : [" + mHeaderName + "]");
        log("source : [" + mSourceName + "]");

        findNativeMethods();

        return true;
    }

    private void findNativeMethods() {
        mClazz.getEnclosedElements()
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
            FileObject fileObject = mEnv.filer.createResource(StandardLocation.SOURCE_OUTPUT, "", mHeaderName);
            log("write header file [" + fileObject.getName() + "]");
            w = fileObject.openWriter();


            w.write(FileTemplate.withType(FileTemplate.Type.JNI_HEADER_TEMPLATE)
                                .add("include_guard", "_Included_" + mJNIClassName)
                                .add("consts", generateConstantsDefinition())
                                .add("methods", generateFunctions(false))
                                .create()
            );

            w.close();
        } catch (IOException e) {
            warn("generate header file " + mHeaderName + " failed!");
        } finally {
            closeSilently(w);
        }
    }

    public void generateSource() {
        Writer w = null;
        try {
            FileObject fileObject = mEnv.filer.createResource(StandardLocation.SOURCE_OUTPUT, "", mSourceName);
            log("write source file [" + fileObject.getName() + "]");
            w = fileObject.openWriter();

            w.write(FileTemplate.withType(FileTemplate.Type.JNI_CPP_TEMPLATE)
                                .add("header", mHeaderName)
                                .add("full_java_class_name", mClassName)
                                .add("full_slash_class_name", mSlashClassName)
                                .add("full_native_class_name", mJNIClassName)
                                .add("methods", generateFunctions(true))
                                .add("jni_method_struct", generateJniNativeMethodStruct())
                                .create()
            );
        } catch (IOException e) {
            warn("generate source file " + mSourceName + " failed");
        } finally {
            closeSilently(w);
        }
    }

    private String generateConstantsDefinition() {
        StringBuilder sb = new StringBuilder();
        //if this field is a compile-time constant value it's
        //value will be returned, otherwise null will be returned.
        mClazz.getEnclosedElements()
              .stream()
              .filter(e -> e.getKind() == ElementKind.FIELD)
              .map(e -> (VariableElement) e)
              .filter(ve -> ve.getConstantValue() != null)
              .forEach(ve -> {
                  //if this field is a compile-time constant value it's
                  //value will be returned, otherwise null will be returned.
                  Object constValue = ve.getConstantValue();

                  sb.append(FileTemplate.withType(FileTemplate.Type.CONSTANT_TEMPLATE)
                                        .add("type", mHelper.toNativeType(ve.asType()))
                                        .add("name", ve.getSimpleName().toString())
                                        .add("full_class_name", mJNIClassName)
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
            sb.append(FileTemplate.withType(FileTemplate.Type.JNINATIVEMETHOD_STRUCT_TEMPLATE)
                                  .add("method_name", m.getSimpleName().toString())
                                  .add("signature", mHelper.getBinaryMethodSignature(m))
                                  .add("comma", i != methodLen - 1 ? "," : "")
                                  .create()
            );
        }
        return sb.toString();
    }

    private static void closeSilently(Closeable c) {
        if (c == null) return;
        try {
            c.close();
        } catch (IOException e) {

        }
    }

    private String generateFunctions(boolean isSource) {
        StringBuilder sb = new StringBuilder();
        for (Element m : mMethods) {
            ExecutableElement e = (ExecutableElement) m;
            String result =
                    FileTemplate.withType(FileTemplate.Type.NATIVE_METHOD_DECLARE_TEMPLATE)
                                .add("full_java_class_name", mJNIClassName)
                                .add("modifiers", mHelper.getMethodModifiers(e))
                                .add("java_return_type", e.getReturnType().toString())
                                .add("method_name", e.getSimpleName().toString())
                                .add("java_parameters", mHelper.getJavaMethodParam(e))
                                .add("jni_return_type", mHelper.toJNIType(e.getReturnType()))
                                .add("method_signature", mHelper.getMethodSignature(e))
                                .add("native_parameters", mHelper.getNativeMethodParam(e))
                                .add("end", isSource ? "" : ";\n")
                                .create();
            if (isSource) {
                result = generateFunctionWithReturnStatement(result, e);
            }
            sb.append(result).append("\n");
        }
        return sb.toString();
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
            returnStatement.append(mHelper.getReturnStatement(m));
        }
        return template.add("default_return_statement", returnStatement.toString())
                       .create();
    }
}
