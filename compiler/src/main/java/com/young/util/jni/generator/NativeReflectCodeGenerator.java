package com.young.util.jni.generator;

import com.google.common.collect.ArrayListMultimap;
import com.young.jenny.annotation.NativeAccessField;
import com.young.jenny.annotation.NativeReflect;
import com.young.util.jni.generator.template.FileTemplate;

import java.io.IOException;
import java.io.Writer;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;

import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.tools.FileObject;
import javax.tools.StandardLocation;

import static com.sun.javafx.fxml.expression.Expression.add;
import static com.sun.javafx.fxml.expression.Expression.get;
import static com.sun.javafx.fxml.expression.Expression.set;

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

    private final LinkedList<ExecutableElement> mConstructors;
    private final ArrayListMultimap<String, ExecutableElement> mMethods;
    private final ArrayListMultimap<String, Element> mFields;
    private final Set<String> mConsts;
    private final NativeReflect mNativeReflectAnnotation;
    private final String mFileName;

    private int mDummyIndex;

    public NativeReflectCodeGenerator(Environment env, TypeElement clazz) {
        super(env, clazz);
        mConstructors = new LinkedList<>();
        mMethods = ArrayListMultimap.create(16, 16);
        mFields = ArrayListMultimap.create(16, 16);
        mConsts = new HashSet<>();

        NativeReflect annotation = clazz.getAnnotation(NativeReflect.class);
        if (annotation == null) {
            annotation = AnnotationResolver.getDefaultImplementation(NativeReflect.class);
        }
        mNativeReflectAnnotation = annotation;

        mFileName = getCppClassName() + ".h";
    }

    @Override
    public void doGenerate() {
        init();
        writeToFile(FileTemplate
                .withType(FileTemplate.Type.NATIVE_REFLECT_SKELETON)
                .add("cpp_class_name", getCppClassName())
                .add("consts", generateConstantsDefinition())
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
        return mNativeReflectAnnotation.simpleName() ? mSimpleClassName : mJNIClassName;
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

                  mConsts.add(ve.getSimpleName().toString());
                  sb.append(FileTemplate.withType(FileTemplate.Type.NATIVE_REFLECT_CONSTANT)
                                        .add("type", mHelper.toNativeType(ve.asType()))
                                        .add("name", ve.getSimpleName().toString())
                                        .add("value", HandyHelper.getJNIHeaderConstantValue(constValue))
                                        .create()
                  );
              });
        return sb.toString();
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
               .filter(e -> !mConsts.contains(e.getSimpleName().toString()))
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
               .filter(e -> !mConsts.contains(e.getSimpleName().toString()))
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
                    final String returnType = mHelper.toJNIType(m.getReturnType());
                    log("m=" + m.getSimpleName() + " r=" + m.getReturnType() + " re=" + returnType);
                    final String returnStatement = FileTemplate
                            .withType(FileTemplate.Type.NATIVE_REFLECT_METHOD_RETURN)
                            .add("static", isStatic ? "Static" : "")
                            .add("param_value", getJniMethodParamVal(m))
                            .add("clazz_or_obj", isStatic ? "sClazz" : "mJavaObjectReference")
                            .add("type", getTypeForJniCall(m.getReturnType()))
                            .create();

                    sb.append(FileTemplate
                            .withType(FileTemplate.Type.NATIVE_REFLECT_METHODS)
                            .add("name", m.getSimpleName().toString())
                            .add("method_id", getMethodName(m, mDummyIndex++))
                            .add("_static", isStatic ? "static " : "")
                            .add("_const", isStatic ? "" : "const ")
                            .add("return_type", returnType)
                            .add("param_declare", getJniMethodParam(m))
                            .add("return", m.getReturnType().getKind() != TypeKind.VOID ? "return " : "")
                            .add("return_statement", !returnTypeNeedCast(returnType)
                                    ? returnStatement
                                    : FileTemplate.withType(FileTemplate.Type.REINTERPRET_CAST)
                                                  .add("type", returnType)
                                                  .add("expression", returnStatement)
                                                  .create())
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
               .filter(e -> !mConsts.contains(e.getSimpleName().toString()))
               .forEach(f -> {
                   final boolean isStatic = f.getModifiers().contains(Modifier.STATIC);
                   final String camelCaseName = camelCase(f.getSimpleName().toString());
                   final String returnType = mHelper.toJNIType(f.asType());
                   final EnumSet<GetterSetter> getterSetters = hasGetterSetter(f);

                   final Map<String, String> r = new HashMap<>();
                   r.put("_static", isStatic ? "static " : "");
                   r.put("return_type", returnType);
                   r.put("camel_case_name", camelCaseName);
                   r.put("_const", isStatic ? "" : "const ");
                   r.put("static", isStatic ? "Static" : "");
                   r.put("_type", getTypeForJniCall(f.asType()));
                   r.put("clazz_or_obj", isStatic ? "sClazz" : "mJavaObjectReference");
                   r.put("field_id", getFieldName(f, mDummyIndex++));
                   r.put("name", f.getSimpleName().toString());
                   r.put("type", mHelper.toJNIType(f.asType()));

                   final String returnStatement = FileTemplate.withType(
                           FileTemplate.Type.NATIVE_REFLECT_FIELDS_GETTER_RETURN)
                                                              .create(r);

                   sb.append(FileTemplate
                           .withType(FileTemplate.Type.NATIVE_REFLECT_FIELDS_GETTER_SETTER)
                           .add("getter", !getterSetters.contains(GetterSetter.GETTER)
                                   ? ""
                                   : FileTemplate.withType(FileTemplate.Type.NATIVE_REFLECT_FIELDS_GETTER)
                                                 .add("return_statement", !returnTypeNeedCast(returnType)
                                                         ? returnStatement
                                                         : FileTemplate.withType(FileTemplate.Type.REINTERPRET_CAST)
                                                                       .add("type", returnType)
                                                                       .add("expression", returnStatement)
                                                                       .create())
                                                 .create(r))
                           .add("setter", !getterSetters.contains(GetterSetter.SETTER)
                                   ? ""
                                   : FileTemplate.withType(FileTemplate.Type.NATIVE_REFLECT_FIELDS_SETTER)
                                                 .create(r))
                           .create()
                   );
               });

        return sb.toString();
    }

    private enum GetterSetter {
        GETTER, SETTER
    }

    private EnumSet<GetterSetter> hasGetterSetter(Element field) {
        boolean getter = false;
        boolean setter = false;

        boolean auto = true;
        NativeAccessField annotation = field.getAnnotation(NativeAccessField.class);
        if (annotation != null) {
            if (!annotation.auto()) {
                auto = false;
                getter = annotation.getter();
                setter = annotation.setter();
            }
        }

        if (auto) {
            final String camelCaseName = camelCase(field.getSimpleName().toString());
            setter = !mMethods.containsKey("set" + camelCaseName);

            final String type = mHelper.toJNIType(field.asType());
            getter = !mMethods.containsKey("get" + camelCaseName);
            if ("jboolean".equals(type)) {
                getter &= !mMethods.containsKey("is" + camelCaseName);
            }
        }

        if (getter && setter) {
            return EnumSet.of(GetterSetter.GETTER, GetterSetter.SETTER);
        } else if (getter) {
            return EnumSet.of(GetterSetter.GETTER);
        } else if (setter) {
            return EnumSet.of(GetterSetter.SETTER);
        } else {
            return EnumSet.noneOf(GetterSetter.class);
        }
    }

    private boolean returnTypeNeedCast(String returnType) {
        switch (returnType) {
            case "jclass":
            case "jstring":
            case "jarray":
            case "jobjectArray":
            case "jbooleanArray":
            case "jbyteArray":
            case "jcharArray":
            case "jshortArray":
            case "jintArray":
            case "jlongArray":
            case "jfloatArray":
            case "jdoubleArray":
            case "jthrowable":
            case "jweak":
                return true;
            default:
                //primitive type or jobject
                return false;
        }
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
               .filter(e -> !mConsts.contains(e.getSimpleName().toString()))
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
        if (mHelper.isNestedClass(mClazz)) {
            Element enclosingElement = mClazz.getEnclosingElement();
            //nested class has an this$0 in its constructor
            sb.append(", ")
              .append(mHelper.toJNIType(enclosingElement.asType()))
              .append(" ")
              .append("enclosingClass");
        }
        m.getParameters().forEach(p -> {
            sb.append(", ")
              .append(mHelper.toJNIType(p.asType()))
              .append(" ")
              .append(p.getSimpleName());
        });
        return sb.toString();
    }

    private String getJniMethodParamVal(ExecutableElement m) {
        StringBuilder sb = new StringBuilder(64);
        if (mHelper.isNestedClass(mClazz)) {
            //nested class has an this$0 in its constructor
            sb.append(", ")
              .append("enclosingClass");
        }
        m.getParameters().forEach(p -> {
            sb.append(", ")
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
