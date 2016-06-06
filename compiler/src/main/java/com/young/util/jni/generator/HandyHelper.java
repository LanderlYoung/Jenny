package com.young.util.jni.generator;

import java.util.Comparator;
import java.util.Iterator;
import java.util.Stack;

import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.ArrayType;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.NoType;
import javax.lang.model.type.TypeMirror;

/**
 * Author: landerlyoung@gmail.com
 * Date:   2014-12-17
 * Time:   20:19
 * Life with passion. Code with creativity!
 */
public final class HandyHelper {

    private final Environment mEnv;

    public HandyHelper(Environment env) {
        mEnv = env;
    }

    public String getMethodSignature(ExecutableElement method) {
        return new Signature(method, false).toString();
    }

    public String getTypeSignature(TypeMirror type) {
        return new Signature(type, false).toString();
    }

    //same as java, but do not change '$' to '/' for inner classes.
    public String getBinaryMethodSignature(ExecutableElement method) {
        return new Signature(method, true).toString();
    }

    public String getBinaryTypeSignature(TypeMirror type) {
        return new Signature(type, true).toString();
    }

    /**
     * @return like com.example_package.SomeClass$InnerClass
     */
    public String getClassName(Element clazz) {
        Stack<String> className = new Stack<>();
        StringBuilder sb = new StringBuilder();
        Element e = clazz;

        while (e != null && (ElementKind.CLASS.equals(e.getKind())
                || ElementKind.INTERFACE.equals(e.getKind()))) {
            className.push(e.getSimpleName().toString());
            e = e.getEnclosingElement();
        }

        PackageElement pkg = mEnv.elementUtils.getPackageOf(clazz);
        if (pkg != null) {
            String pkgName = pkg.getQualifiedName().toString();
            if (pkgName.length() > 0) {
                sb.append(pkgName);
                sb.append('.');
            }
        }

        while (!className.empty()) {
            sb.append(className.pop());
            sb.append('$');
        }
        sb.deleteCharAt(sb.length() - 1);
        return sb.toString();
    }

    /**
     * @return like com/example_package/SomeClass$InnerClass
     */
    public String getSlashClassName(String className) {
        return className.replace('.', '/');
    }

    public String getJNIClassName(Class<?> c) {
        return toJNIClassName(c.getName());
    }

    /**
     * @param className
     * @return like com_example_1package_SomeClass_InnerClass
     */
    public String toJNIClassName(String className) {
        if (className == null) return null;
        return className.replace("_", "_1")
                        .replace(".", "_")
                        .replace('$', '_'); //inner class
    }

    public String getMethodModifiers(ExecutableElement m) {
        StringBuilder sb = new StringBuilder();
        m.getModifiers()
         .stream()
         .filter(modifier ->
                 modifier == Modifier.PUBLIC
                         || modifier == Modifier.PROTECTED
                         || modifier == Modifier.PRIVATE
                         || modifier == Modifier.FINAL
                         || modifier == Modifier.STATIC
         )
         .sorted(Comparator.naturalOrder())
         .forEach(modifier -> {
             sb.append(modifier.toString().toLowerCase());
             sb.append(' ');
         });
        if (sb.length() > 0) {
            //delete the last space
            sb.replace(sb.length() - 1, sb.length(), "");
        }
        return sb.toString();
    }

    public String getJavaMethodParam(ExecutableElement m) {
        StringBuilder sb = new StringBuilder();
        Iterator<? extends VariableElement> it = m.getParameters().iterator();
        while (it.hasNext()) {
            VariableElement v = it.next();
            sb.append(v.asType().toString())
              .append(' ')
              .append(v.getSimpleName());
            if (it.hasNext()) {
                sb.append(", ");
            }
        }
        return sb.toString();
    }

    /**
     * @param v
     *
     * @return
     */
    public static String getJNIHeaderConstantValue(Object v) {
        if (v == null) {
            return "";
        }
        Class<?> clazz = v.getClass();
        if (Boolean.class == clazz) {
            return (Boolean) v ? "JNI_TRUE" : "JNI_FALSE";
        } else if (clazz == Byte.class
                || clazz == Short.class
                || clazz == Integer.class
                || clazz == Character.class
                || clazz == Long.class
                || clazz == Float.class
                || clazz == Double.class) {
            return v + "";
        } else if (clazz == String.class) {
            return "\"" + v + "\"";
        } else {
            return "";
        }
    }

    public String getReturnStatement(ExecutableElement e) {
        StringBuilder sb = new StringBuilder();
        sb.append("return ");
        String typeName = e.getReturnType().toString();
        if (String.class.getName().equals(typeName)) {
            sb.append("env->NewStringUTF(\"Hello From Jenny\")");
        } else if (int.class.getName().equals(typeName)
                || byte.class.getName().equals(typeName)
                || char.class.getName().equals(typeName)
                || short.class.getName().equals(typeName)
                || long.class.getName().equals(typeName)
                || float.class.getName().equals(typeName)
                || double.class.getName().equals(typeName)) {
            sb.append("0");
        } else if (boolean.class.getName().equals(typeName)) {
            sb.append("JNI_FALSE");
        } else if (void.class.getName().equals(typeName)) {
            //eat that space
            sb.replace(sb.length() - 1, sb.length(), "");
        } else {
            sb.append("nullptr");
        }
        sb.append(";");
        return sb.toString();
    }

    public String getNativeMethodParam(ExecutableElement m) {
        StringBuilder sb = new StringBuilder();
        sb.append("JNIEnv *env");

        if (m.getModifiers().contains(Modifier.STATIC)) {
            sb.append(", jclass clazz");
        } else {
            sb.append(", jobject thiz");
        }

        m.getParameters().stream().forEach(ve -> {
            sb.append(", ");
            sb.append(toJNIType(ve.asType()));
            sb.append(' ');
            sb.append(ve.getSimpleName().toString());
        });
        return sb.toString();
    }

    private boolean instanceOf(Class<?> clazz, TypeMirror t) {
        String clazzName = clazz.getName();
        while (!clazzName.equals(t.toString())) {
            Element base = mEnv.typeUtils.asElement(t);
            if (base instanceof TypeElement) {
                TypeMirror superClazz = ((TypeElement) base).getSuperclass();
                if (superClazz instanceof NoType) return false;
                t = superClazz;
            } else {
                return false;
            }
        }
        return true;
    }

    public String toJNIType(TypeMirror t) {
        if (t == null) return "";

        final String jthrowable = "jthrowable";

        //check if t is a subclass of java.lang.Throwable
        if (instanceOf(Throwable.class, t)) {
            return jthrowable;
        }

        final String c = t.toString();
        switch (c) {
            //void type
            case "void":
                return "void";
            //primitive type
            case "char":
            case "boolean":
            case "byte":
            case "short":
            case "int":
            case "long":
            case "float":
            case "double":
                return 'j' + c;

            //array type
            case "char[]":
            case "boolean[]":
            case "byte[]":
            case "short[]":
            case "int[]":
            case "long[]":
            case "float[]":
            case "double[]":
                return 'j' + c.substring(0, c.length() - 2) + "Array";

            //native build in type
            case "java.lang.String":
                return "jstring";
            case "java.lang.Class":
                return "jclass";

            default:
                if (c.endsWith("[]")) {
                    //java.lang.Object[]
                    return "jobjectArray";
                } else {
                    return "jobject";
                }
        }
    }

    public String toNativeType(TypeMirror t) {
        if (t == null) return null;

        return toNativeType(t.toString());
    }

    public String toNativeType(String className) {
        switch (className) {
            //void type
            case "void":
                return "void";
            //primitive type
            case "short":
            case "int":
            case "long":
            case "float":
            case "double":
            case "char":
            case "boolean":
            case "byte":
                return "j" + className;
            case "java.lang.String":
                return "char *";
            default:
                return null;
        }
    }

    private class Signature {
        private final boolean mIsNative;
        private final ExecutableElement mMethod;
        private final TypeMirror mType;
        private final StringBuilder mCache;

        private Signature(ExecutableElement method, TypeMirror type, boolean isNative) {
            mMethod = method;
            mType = type;
            mIsNative = isNative;
            mCache = new StringBuilder();
        }

        public Signature(ExecutableElement method, boolean isNative) {
            this(method, null, isNative);

        }

        public Signature(TypeMirror type, boolean isNative) {
            this((ExecutableElement) null, type, isNative);
        }

        //same as java
        private String getMethodSignature() {
            mCache.append('(');
            if (mMethod.getSimpleName().contentEquals("<init>")) {
                //constructor
                Element clazz = mMethod.getEnclosingElement();
                Element enclosingClazz;
                if (clazz != null
                        && !clazz.getModifiers().contains(Modifier.STATIC)
                        && (enclosingClazz = clazz.getEnclosingElement()) != null) {
                    //generate this$0 param for nested class
                    getSignatureClassName(enclosingClazz.asType());
                }
            }
            for (VariableElement param : mMethod.getParameters()) {
                getSignatureClassName(param.asType());
            }
            mCache.append(')');
            getSignatureClassName(mMethod.getReturnType());
            return mCache.toString();
        }

        private void getSignatureClassName(TypeMirror type) {
            while (type instanceof ArrayType) {
                mCache.append('[');
                type = ((ArrayType) type).getComponentType();
            }
            String className;
            if (type instanceof DeclaredType) {
                className = getClassName(((DeclaredType) type).asElement());
            } else {
                className = type.toString();
            }

            getObjectSignatureClassName(className);
        }

        private void getObjectSignatureClassName(String type) {
            if ("char".equals(type)) {
                mCache.append('C');
            } else if ("byte".equals(type)) {
                mCache.append('B');
            } else if ("short".equals(type)) {
                mCache.append('S');
            } else if ("int".equals(type)) {
                mCache.append('I');
            } else if ("long".equals(type)) {
                mCache.append('J');
            } else if ("float".equals(type)) {
                mCache.append('F');
            } else if ("double".equals(type)) {
                mCache.append('D');
            } else if ("boolean".equals(type)) {
                mCache.append('Z');
            } else if ("void".equals(type)) {
                mCache.append('V');
            } else {
                if (mIsNative) {
                    mCache.append('L').append(type.replace('.', '/')).append(';');
                } else {
                    mCache.append('L')
                          .append(type.replace('.', '/').replace('$', '/'))
                          .append(';');
                }
            }
        }

        @Override
        public String toString() {
            if (mMethod != null) {
                return getMethodSignature();
            } else {
                getSignatureClassName(mType);
                return mCache.toString();
            }
        }
    }
}
