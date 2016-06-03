package com.young.util.jni.generator;

import com.young.jenny.annotation.NativeCode;
import com.young.util.jni.JNIHelper;
import com.young.util.jni.generator.template.FileTemplate;
import com.young.util.jni.generator.template.FileTemplateLoader;

import org.apache.commons.lang3.text.StrSubstitutor;

import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.tools.Diagnostic;
import javax.tools.FileObject;
import javax.tools.StandardLocation;

import java.io.Closeable;
import java.io.IOException;
import java.io.Writer;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Author: LanderlYoung
 * Date:   2014-12-17
 * Time:   16:03
 * Life with passion. Code with creativity!
 */
public class CppCodeGenerator implements Runnable {
    private final Environment mEnv;
    private final TypeElement mClazz;
    private List<Element> mMethods;
    private final HandyHelper mHelper;
    private final FileTemplateLoader mFileTemplateLoader;

    //like com.example_package.SomeClass$InnerClass
    private String mClassName;
    //like com_example_1package_SomeClass_InnerClass
    private String mJNIClassName;
    //like com/example_package/SomeClass$InnerClass
    private String mNativeSlashClassName;

    //header file name
    private String mHeaderName;
    //source file Name
    private String mSourceName;

    //DONE HandyHelper.toJNIType throwable
    //DONE Use NativeClass to mark generate, NativeCode to add implements
    //DELETE different constant value for different arch
    //DONE fix get package name
    //DONE support for inner class
    //XXXX support for pure c code
    //DONE file output
    //GOING use file template

    public CppCodeGenerator(Environment env, TypeElement clazz) {
        mEnv = env;
        mClazz = clazz;
        mMethods = new LinkedList<>();
        mHelper = new HandyHelper(env);
        mFileTemplateLoader = new FileTemplateLoader("file-template");
    }

    @Override
    public void run() {
        doGenerate();
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
        mJNIClassName = JNIHelper.toJNIClassName(mClassName);
        mHeaderName = mJNIClassName + ".h";
        mSourceName = mJNIClassName + ".cpp";
        mNativeSlashClassName = JNIHelper.getNativeSlashClassName(mClassName);
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

    private void log(String msg) {
        mEnv.messager.printMessage(Diagnostic.Kind.NOTE, msg);
    }

    private void warn(String msg) {
        mEnv.messager.printMessage(Diagnostic.Kind.WARNING, msg);
    }

    private void error(String msg) {
        mEnv.messager.printMessage(Diagnostic.Kind.ERROR, msg);
    }

    public void generateHeader() {
        Writer w = null;
        try {
            FileObject fileObject = mEnv.filer.createResource(StandardLocation.SOURCE_OUTPUT, "", mHeaderName);
            log("write header file [" + fileObject.getName() + "]");
            w = fileObject.openWriter();

            String headerTemplate = mFileTemplateLoader.loadTemplate(FileTemplate.JNI_HEADER_TEMPLATE.getName());
            Map<String, String> mTemplateMap = new HashMap<>();
            mTemplateMap.put("include_guard", "_Included_" + mJNIClassName);
            mTemplateMap.put("consts", generateConstantsDefinition());
            mTemplateMap.put("methods", generateFunctions(false));

            w.write(
                    StrSubstitutor.replace(headerTemplate, mTemplateMap)
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

            String fileTemplate = mFileTemplateLoader
                    .loadTemplate(FileTemplate.JNI_CPP_TEMPLATE.getName());
            Map<String, String> templateMap = new HashMap<>();
            templateMap.put("header", mHeaderName);
            templateMap.put("full_java_class_name", mClassName);
            templateMap.put("full_slash_class_name", mNativeSlashClassName);
            templateMap.put("full_native_class_name", mJNIClassName);
            templateMap.put("methods", generateFunctions(true));
            templateMap.put("jni_method_struct", generateJniNativeMethodStruct());

            w.write(StrSubstitutor.replace(fileTemplate, templateMap));
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
        String fileTemplate = mFileTemplateLoader
                .loadTemplate(FileTemplate.CONSTANT_TEMPLATE.getName());
        log(fileTemplate);
        Map<String, String> templateMap = new HashMap<>();
        mClazz.getEnclosedElements()
              .stream()
              .filter(e -> e.getKind() == ElementKind.FIELD)
              .map(e -> (VariableElement) e)
              .filter(ve -> ve.getConstantValue() != null)
              .forEach(ve -> {
                  //if this field is a compile-time constant value it's
                  //value will be returned, otherwise null will be returned.
                  Object constValue = ve.getConstantValue();
                  templateMap.clear();
                  templateMap.put("type", mHelper.toNativeType(ve.asType()));
                  templateMap.put("name", ve.getSimpleName().toString());
                  templateMap.put("full_class_name", mJNIClassName);
                  templateMap.put("value", HandyHelper.getJNIHeaderConstantValue(constValue));

                  sb.append(StrSubstitutor.replace(fileTemplate, templateMap));
              });
        return sb.toString();
    }

    private String generateJniNativeMethodStruct() {
        StringBuilder sb = new StringBuilder();
        int methodLen = mMethods.size();
        for (int i = 0; i < methodLen; i++) {
            ExecutableElement m = (ExecutableElement) mMethods.get(i);
            String fileTemplate = mFileTemplateLoader
                    .loadTemplate(FileTemplate.JNINATIVEMETHOD_STRUCT_TEMPLATE.getName());
            Map<String, String> templateMap = new HashMap<>();
            templateMap.put("method_name", m.getSimpleName().toString());
            templateMap.put("signature", mHelper.getBinaryMethodSignature(m));
            templateMap.put("comma", i != methodLen - 1 ? "," : "");

            sb.append(StrSubstitutor.replace(fileTemplate, templateMap));
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
            String result;
            ExecutableElement e = (ExecutableElement) m;
            String template = mFileTemplateLoader
                    .loadTemplate(FileTemplate.NATIVE_METHOD_DECLARE_TEMPLATE.getName());
            Map<String, String> templateMap = new HashMap<>();
            templateMap.put("full_java_class_name", mJNIClassName);
            templateMap.put("modifiers", mHelper.getMethodModifiers(e));
            templateMap.put("java_return_type", e.getReturnType().toString());
            templateMap.put("method_name", e.getSimpleName().toString());
            templateMap.put("java_parameters", mHelper.getJavaMethodParam(e));
            templateMap.put("jni_return_type", mHelper.toJNIType(e.getReturnType()));
            templateMap.put("method_signature", mHelper.getMethodSignature(e));
            templateMap.put("native_parameters", mHelper.getNativeMethodParam(e));
            templateMap.put("end", isSource ? "" : ";\n");
            result = StrSubstitutor.replace(template, templateMap);

            if (isSource) {
                result = generateFunctionWithReturnStatement(result, e);
            }
            sb.append(result).append("\n");
        }
        return sb.toString();
    }

    private String generateFunctionWithReturnStatement(String declare, ExecutableElement m) {
        String template = mFileTemplateLoader
                .loadTemplate(FileTemplate.NATIVE_METHOD_TEMPLATE.getName());
        Map<String, String> templateMap = new HashMap<>();
        templateMap.put("native_method_declare_template", declare);

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
        templateMap.put("default_return_statement", returnStatement.toString());

        return StrSubstitutor.replace(template, templateMap);
    }
}
