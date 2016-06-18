package com.young.util.jni.generator.template;

import com.young.util.jni.generator.IOUtils;

import org.apache.commons.lang3.text.StrSubstitutor;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.CharBuffer;
import java.util.HashMap;
import java.util.Map;

/**
 * Author: landerlyoung@gmail.com
 * Date:   2016-06-03
 * Time:   00:42
 * Life with Passion, Code with Creativity.
 */
public class FileTemplate {
    private final String mTemplate;
    private final Map<String, String> mTemplateParams;

    private static final FileTemplateLoader sFileTemplateLoader = new FileTemplateLoader("file-template");

    private FileTemplate(String template) {
        mTemplate = template;
        mTemplateParams = new HashMap<>();
    }

    public FileTemplate add(String key, String value) {
        mTemplateParams.put(key, value);
        return this;
    }

    public String getTemplate() {
        return mTemplate;
    }

    public String create() {
        return StrSubstitutor.replace(mTemplate, mTemplateParams);
    }

    public String create(Map<String, String> replacements) {
        mTemplateParams.putAll(replacements);
        return create();
    }

    public static FileTemplate withType(Type t) {
        return new FileTemplate(sFileTemplateLoader.loadTemplate(t.getName()));
    }

    public enum Type {
        NATIVE_CPP_SKELETON("native_cpp_skeleton.cpp"),
        JNI_HEADER_TEMPLATE("native_header_skeleton.h"),
        NATIVE_JNI_NATIVE_METHOD_STRUCT("native_jni_nativeMethodsStruct.cpp"),
        NATIVE_METHOD_DECLARE_TEMPLATE("native_method_declare_template.h"),
        NATIVE_METHOD_TEMPLATE("native_method_template.cpp"),
        CONSTANT_TEMPLATE("cpp_constant.h"),
        NATIVE_REFLECT_SKELETON("native_reflect_skeleton.h"),
        NATIVE_REFLECT_CONSTRUCTORS("native_reflect_constructors.h"),
        NATIVE_REFLECT_FIELD_ID_DECLARE("native_reflect_field_id_declare.h"),
        NATIVE_REFLECT_FIELD_ID_INIT("native_reflect_field_id_init.h"),
        NATIVE_REFLECT_FIELDS_GETTER_SETTER("native_reflect_fields_getter_setters.h"),
        NATIVE_REFLECT_STATIC_FIELDS_GETTER_SETTER("native_reflect_static_fields_getter_setters.h"),
        NATIVE_REFLECT_METHOD_ID_DECLARE("native_reflect_method_id_declare.h"),
        NATIVE_REFLECT_METHOD_ID_INIT("native_reflect_method_id_init.h"),
        NATIVE_REFLECT_METHODS("native_reflect_methods.h"),
        NATIVE_REFLECT_STATIC_METHODS("native_reflect_static_methods.h"),
        NATIVE_REFLECT_CONSTANT("native_reflect_constant.h"),
        NATIVE_REFLECT_CPP_STATIC_INIT("native_reflect_cpp_static_init.h");

        private String mName;

        Type(String name) {
            mName = name;
        }

        public String getName() {
            return mName;
        }
    }

    private static class FileTemplateLoader {
        public String mDirectory;

        //we are not running in Android! we are running at desktop environment!
        //YEAH! don't care too much about little memory pressure.
        private final Map<String, String> mInfiniteCache = new HashMap<>();

        public FileTemplateLoader(String directory) {
            mDirectory = directory;
        }

        public String loadTemplate(String fileName) {
            if (mDirectory != null) {
                fileName = mDirectory + File.separatorChar + fileName;
            }
            String result = mInfiniteCache.get(fileName);
            if (result == null) {
                result = readStream(getClass().getClassLoader().getResourceAsStream(fileName));
                mInfiniteCache.put(fileName, result);
            }
            return result;
        }

        private static String readStream(InputStream in) {
            if (in != null) {
                try {
                    InputStreamReader reader = new InputStreamReader(new BufferedInputStream(in));
                    StringBuilder sb = new StringBuilder(in.available());
                    CharBuffer buffer = CharBuffer.allocate(in.available());
                    while (reader.read(buffer) != -1) {
                        buffer.flip();
                        sb.append(buffer);
                        buffer.clear();
                    }
                    return sb.toString();
                } catch (IOException e) {
                    IOUtils.closeSilently(in);
                }
            }
                return null;
        }
    }
}
