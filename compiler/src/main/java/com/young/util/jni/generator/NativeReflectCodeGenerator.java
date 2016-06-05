package com.young.util.jni.generator;

import com.google.common.collect.ArrayListMultimap;
import com.young.util.jni.generator.template.FileTemplate;

import java.io.IOException;
import java.io.Writer;
import java.util.LinkedList;

import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.tools.FileObject;
import javax.tools.StandardLocation;

/**
 * Author: landerlyoung@gmail.com
 * Date:   2016-06-05
 * Time:   00:30
 * Life with Passion, Code with Creativity.
 */
public class NativeReflectCodeGenerator extends AbsCodeGenerator {
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

    final LinkedList<ExecutableElement> mConstructors;
    final ArrayListMultimap<String, ExecutableElement> mMethods;
    final ArrayListMultimap<String, Element> mFields;

    final String mFileName;

    private int mDummyIndex;

    public NativeReflectCodeGenerator(Environment env, TypeElement clazz) {
        super(env, clazz);
        mConstructors = new LinkedList<>();
        mMethods = ArrayListMultimap.create(16, 16);
        mFields = ArrayListMultimap.create(16, 16);
        mFileName = mJNIClassName + ".h";
    }

    public void doGenerate() {
        init();
        writeToFile(FileTemplate
                .withType(FileTemplate.Type.NATIVE_REFLECT_SKELETON)
                .add("cpp_class_name", getCppClassName())
                .add("full_class_name_const", mSlashClassName)
                .add("constructors_id_declare", generateConstructorIdDeclare())
                .add("methods_id_declare", generateMethodIdDeclare())
                .add("fields_id_declare", generateFieldIdDeclare())
                .add("constructors_id_init", generateConstructorIdInit())
                .add("methods_id_init", generateMethodIdInit())
                .add("fields_id_init", generateFieldIdInit())
                .add("constructors", generateConstructors())
                .add("methods", generateMethods())
                .add("fields_getter_setter", generateFields())
                .add("static_declare", generateCppStaticDeclare())
                .create()
        );
    }

    private String getCppClassName() {
        return mJNIClassName;
    }

    private String generateConstructorIdDeclare() {
        mDummyIndex = 0;
        StringBuilder sb = new StringBuilder();
        mConstructors.stream()
                     .forEach(c -> {
                         sb.append(FileTemplate
                                 .withType(FileTemplate.Type.NATIVE_REFLECT_METHOD_ID_DECLARE)
                                 .add("name", getConstructorName(c, mDummyIndex++))
                                 .create()
                         );
                     });
        return sb.toString();
    }

    private String generateMethodIdDeclare() {
        mDummyIndex = 0;
        StringBuilder sb = new StringBuilder();
        mMethods.values()
                .stream()
                .forEach(m -> {
                    sb.append(FileTemplate
                            .withType(FileTemplate.Type.NATIVE_REFLECT_METHOD_ID_DECLARE)
                            .add("name", getMethodName(m, mDummyIndex++))
                            .create()
                    );
                });
        return sb.toString();
    }

    private String generateFieldIdDeclare() {
        mDummyIndex = 0;
        StringBuilder sb = new StringBuilder();
        mFields.values()
               .stream()
               .forEach(f -> {
                   sb.append(FileTemplate
                           .withType(FileTemplate.Type.NATIVE_REFLECT_FIELD_ID_DECLARE)
                           .add("name", getFieldName(f, mDummyIndex++))
                           .create()
                   );
               });

        return sb.toString();
    }

    private String generateConstructorIdInit() {
        mDummyIndex = 0;
        StringBuilder sb = new StringBuilder();
        mConstructors.stream()
                     .forEach(c -> {
                         sb.append(FileTemplate
                                 .withType(FileTemplate.Type.NATIVE_REFLECT_METHOD_ID_INIT)
                                 .add("name", getConstructorName(c, mDummyIndex++))
                                 .add("static", "")
                                 .add("method_name", "<init>")
                                 .add("method_signature", mHelper.getBinaryMethodSignature(c))
                                 .create()
                         );
                     });
        return sb.toString();
    }

    private String generateMethodIdInit() {
        mDummyIndex = 0;
        StringBuilder sb = new StringBuilder();
        mMethods.values()
                .stream()
                .forEach(m -> {
                    sb.append(FileTemplate
                            .withType(FileTemplate.Type.NATIVE_REFLECT_METHOD_ID_INIT)
                            .add("name", getMethodName(m, mDummyIndex++))
                            .add("static", m.getModifiers().contains(Modifier.STATIC) ? "Static" : "")
                            .add("method_name", m.getSimpleName().toString())
                            .add("method_signature", mHelper.getBinaryMethodSignature(m))
                            .create()
                    );
                });
        return sb.toString();
    }

    private String generateFieldIdInit() {
        mDummyIndex = 0;
        StringBuilder sb = new StringBuilder();
        mFields.values()
               .stream()
               .forEach(f -> {
                   sb.append(FileTemplate
                           .withType(FileTemplate.Type.NATIVE_REFLECT_FIELD_ID_INIT)
                           .add("name", getFieldName(f, mDummyIndex++))
                           .add("static", f.getModifiers().contains(Modifier.STATIC) ? "Static" : "")
                           .add("field_name", f.getSimpleName().toString())
                           .add("field_signature", mHelper.getBinaryTypeSignature(f.asType()))
                           .create()
                   );
               });

        return sb.toString();
    }

    private String generateConstructors() {
        mDummyIndex = 0;
        StringBuilder sb = new StringBuilder();
        mConstructors.stream()
                     .forEach(c -> {
                         sb.append(FileTemplate
                                 .withType(FileTemplate.Type.NATIVE_REFLECT_CONSTRUCTORS)
                                 .add("constructor_method_id", getConstructorName(c, mDummyIndex++))
                                 .add("param_declare", getJniMethodParam(c))
                                 .add("param_val", getJniMethodParamVal(c))
                                 .create()
                         );
                     });
        return sb.toString();
    }

    private String generateMethods() {
        mDummyIndex = 0;
        StringBuilder sb = new StringBuilder();
        mMethods.values()
                .stream()
                .forEach(m -> {
                    final boolean isStatic = m.getModifiers().contains(Modifier.STATIC);
                    sb.append(FileTemplate
                            .withType(FileTemplate.Type.NATIVE_REFLECT_METHODS)
                            .add("name", m.getSimpleName().toString())
                            .add("method_id", getMethodName(m, mDummyIndex++))
                            .add("static_modifier", isStatic ? "static " : "")
                            .add("static", isStatic ? "Static" : "")
                            .add("return_value", mHelper.toJNIType(m.getReturnType()))
                            .add("param_declare", getJniMethodParam(m))
                            .add("param_value", getJniMethodParamVal(m))
                            .add("clazz_or_obj", isStatic ? "sClazz" : "mJavaObjectReference")
                            .add("type", getTypeForJniCall(m.getReturnType()))
                            .add("return", m.getReturnType().getKind() != TypeKind.VOID ? "return " : "")
                            .create()
                    );
                });
        return sb.toString();
    }

    private String generateFields() {
        mDummyIndex = 0;
        StringBuilder sb = new StringBuilder();
        mFields.values()
               .stream()
               .forEach(f -> {
                   final boolean isStatic = f.getModifiers().contains(Modifier.STATIC);
                   sb.append(FileTemplate
                           .withType(FileTemplate.Type.NATIVE_REFLECT_FIELDS_GETTER_SETTER)
                           .add("return_val", mHelper.toJNIType(f.asType()))
                           .add("camel_case_name", camelCase(f.getSimpleName().toString()))
                           .add("static", isStatic ? "Static" : "")
                           .add("_type", getTypeForJniCall(f.asType()))
                           .add("clazz_or_obj", isStatic ? "sClazz" : "mJavaObjectReference")
                           .add("field_id", getFieldName(f, mDummyIndex++))
                           .add("name", f.getSimpleName().toString())
                           .add("type", mHelper.toJNIType(f.asType()))
                           .create()
                   );
               });

        return sb.toString();
    }

    private String generateCppStaticDeclare() {
        StringBuilder sb = new StringBuilder(2048);

        mDummyIndex = 0;
        mConstructors.stream()
                     .forEach(c -> {
                         sb.append(FileTemplate
                                 .withType(FileTemplate.Type.NATIVE_REFLECT_CPP_STATIC_INIT)
                                 .add("type", "jmethodID")
                                 .add("cpp_class_name", getCppClassName())
                                 .add("name", getConstructorName(c, mDummyIndex++))
                                 .create()
                         );
                     });

        mDummyIndex = 0;
        mMethods.values().stream()
                .forEach(m -> {
                    sb.append(FileTemplate
                            .withType(FileTemplate.Type.NATIVE_REFLECT_CPP_STATIC_INIT)
                            .add("type", "jmethodID")
                            .add("cpp_class_name", getCppClassName())
                            .add("name", getMethodName(m, mDummyIndex++))
                            .create());
                });

        mDummyIndex = 0;
        mFields.values().stream()
               .forEach(f -> {
                   sb.append(FileTemplate
                           .withType(FileTemplate.Type.NATIVE_REFLECT_CPP_STATIC_INIT)
                           .add("type", "jfieldID")
                           .add("cpp_class_name", getCppClassName())
                           .add("name", getFieldName(f, mDummyIndex++))
                           .create());
               });

        return sb.toString();
    }

    private String getConstructorName(ExecutableElement e, int index) {
        return "sConstruct_" + index;
    }

    private String getMethodName(ExecutableElement e, int index) {
        return "sMethod_" + e.getSimpleName() + "_" + index;
    }

    private String getFieldName(Element e, int index) {
        return "sField_" + e.getSimpleName() + "_" + index;
    }

    private void writeToFile(String content) {
        Writer w = null;
        try {
            FileObject fileObject = mEnv.filer.createResource(StandardLocation.SOURCE_OUTPUT, PKG_NAME, mFileName);
            log("write native reflect file [" + fileObject.getName() + "]");
            w = fileObject.openWriter();
            w.write(content);
            w.close();
        } catch (IOException e) {
            warn("generate header file " + mFileName + " failed!");
        } finally {
            IOUtils.closeSilently(w);
        }
    }

    private void init() {
        findConstructors();
        findMethods();
        findFields();
    }

    private void findConstructors() {
        mClazz.getEnclosedElements()
              .stream()
              .filter(e -> e.getKind() == ElementKind.CONSTRUCTOR)
              .map(e -> (ExecutableElement) e)
              .forEach(mConstructors::add);
    }

    private void findMethods() {
        mClazz.getEnclosedElements()
              .stream()
              .filter(e -> e.getKind() == ElementKind.METHOD)
              .map(e -> (ExecutableElement) e)
              .forEach(e ->
                      mMethods.put(
                              e.getSimpleName().toString(),
                              e
                      )
              );
    }

    private void findFields() {
        mClazz.getEnclosedElements()
              .stream()
              .filter(e -> e.getKind() == ElementKind.FIELD)
              .forEach(e ->
                      mFields.put(
                              e.getSimpleName().toString(),
                              e)
              );
    }

    private String getJniMethodParam(ExecutableElement m) {
        StringBuilder sb = new StringBuilder(64);
        m.getParameters().forEach(p -> {
            sb.append(" ,")
              .append(mHelper.toJNIType(p.asType()))
              .append(" ")
              .append(p.getSimpleName());
        });
        return sb.toString();
    }

    private String getJniMethodParamVal(ExecutableElement m) {
        StringBuilder sb = new StringBuilder(64);
        m.getParameters().forEach(p -> {
            sb.append(" ,")
              .append(p.getSimpleName());
        });
        return sb.toString();
    }

    private String getTypeForJniCall(TypeMirror type) {
        String result;
        TypeKind k = type.getKind();
        if (k.isPrimitive() || k == TypeKind.VOID) {
            result = k.name().toLowerCase();
        } else {
            result = "object";
        }
        return camelCase(result);
    }

    private String camelCase(String s) {
        return Character.toUpperCase(s.charAt(0)) + s.substring(1);
    }
}
