package com.young.util.jni.generator;

import java.util.Stack;

import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.ArrayType;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.NoType;
import javax.lang.model.type.TypeMirror;

/**
 * Author: LanderlYoung
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

    //same as java, but do not change '$' to '/' for inner classes.
    public String getBinaryMethodSignature(ExecutableElement method) {
        return new Signature(method, true).toString();
    }

    /**
     * @return like com.example_package.SomeClass$InnerClass
     */
    public String getClassName(Element clazz) {
        Stack<String> className = new Stack<String>();
        StringBuilder sb = new StringBuilder();
        Element e = clazz;

        while (e != null && ElementKind.CLASS.equals(e.getKind())
                || ElementKind.INTERFACE.equals(e.getKind())) {
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
     * @param v
     *
     * @return
     */
    public static String getJNIHeaderConstantValue(Object v, @SuppressWarnings("unused") int arch) {
        if (v == null) {
            return "";
        }
        Class<?> clazz = v.getClass();
        if (Boolean.class == clazz) {
            return (Boolean) v ? "1L" : "0L";
        } else if (clazz == Byte.class ||
                clazz == Short.class ||
                clazz == Integer.class) {
            return v + "L";
        } else if (clazz == Character.class) {
            return (int) (Character) v + "L";
        } else if (clazz == Long.class) {
            return v + "LL";
        } else if (clazz == Float.class) {
            return v + "f";
        } else if (clazz == Double.class) {
            return v + "";
        } else if (clazz == String.class) {
            return "\"" + v + "\"";
        }
        return "";
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

        final String c = t.toString();
        switch (c) {
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
                return "j" + c;
            case "java.lang.String":
                return "char *";
            default:
                return null;
        }
    }

    private class Signature {
        private final boolean mIsNative;
        private final ExecutableElement mMethod;
        private final StringBuilder mCache;

        public Signature(ExecutableElement method, boolean isNative) {
            mMethod = method;
            mIsNative = isNative;
            mCache = new StringBuilder();
        }

        //same as java
        private String getMethodSignature() {
            mCache.append('(');
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
            return getMethodSignature();
        }
    }
}
