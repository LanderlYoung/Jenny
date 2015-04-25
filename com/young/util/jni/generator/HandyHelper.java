package com.young.util.jni.generator;

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
import java.util.Stack;

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
            if(pkgName.length() > 0) {
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


    public String toJNIType(TypeMirror t) {
        if (t == null) return "";
        final String c = t.toString();
        final String throwable = "java.lang.Throwable";
        final String jthrowable = "jthrowable";

        //check if t is a subclass of java.lang.Throwable
        if (throwable.equals(c)) {
            return jthrowable;
        } else {
            Element base = mEnv.typeUtils.asElement(t);
            while (base instanceof TypeElement) {
                TypeMirror sup = ((TypeElement) base).getSuperclass();
                if (sup instanceof NoType) break;
                if (throwable.equals(sup.toString())) {
                    return jthrowable;
                }
                base = mEnv.typeUtils.asElement(sup);
            }
        }

        //for android, use java language level 6, can not use string case!!
        if ("void".equals(c)) {
            return "void";
        } else if ("char".equals(c) ||
                "boolean".equals(c) ||
                "byte".equals(c) ||
                "short".equals(c) ||
                "int".equals(c) ||
                "long".equals(c) ||
                "float".equals(c) ||
                "double".equals(c)) {
            return 'j' + c;
        } else if ("char[]".equals(c) ||
                "boolean[]".equals(c) ||
                "byte[]".equals(c) ||
                "short[]".equals(c) ||
                "int[]".equals(c) ||
                "long[]".equals(c) ||
                "float[]".equals(c) ||
                "double[]".equals(c)) {
            return 'j' + c.substring(0, c.length() - 2) + "Array";
        } else if (c.endsWith("[][]")) {
            //multi dimension array
            return "jobjectArray";
        } else if (c.endsWith("[]")) {
            //java.lang.Object[]
            return "jobjectArray";
        } else if (c.equals("java.lang.String")) {
            return "jstring";
        } else if (c.equals("java.lang.Class")) {
            return "jclass";
        }
       /*
        else if ("java.lang.Throwable".equals(c)) {
            return "jthrowable";
        }
        */
        else {
            return "jobject";
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
