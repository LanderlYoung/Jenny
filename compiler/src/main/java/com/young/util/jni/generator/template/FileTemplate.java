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
        CONSTANT_TEMPLATE("cpp_constant.h"),
        REINTERPRET_CAST("reinterpret_cast.h"),
        NATIVE_CPP_SKELETON("native_cpp_skeleton.cpp"),
        CPP_DYNAMIC_JNI_REGISTER("cpp_dynamic_jni_register.cpp"),
        JNI_ONLOAD_DECLARE("jni_onload_declare.h"),
        JNI_ONLOAD_IMPL("jni_onload_impl.cpp"),
        CPP_EXPORT_MARCO_BEGIN("cpp_export_marco_begin.h"),
        CPP_EXPORT_MARCO_END("cpp_export_marco_end.h"),
        JNI_HEADER_TEMPLATE("native_header_skeleton.h"),
        ANDROID_LOG_MARCOS("android_log_marcos.h"),
        NATIVE_JNI_NATIVE_METHOD_STRUCT("native_jni_nativeMethodsStruct.cpp"),
        NATIVE_METHOD_DECLARE_TEMPLATE("native_method_declare_template.h"),
        NATIVE_METHOD_TEMPLATE("native_method_template.cpp"),
        NATIVE_REFLECT_SKELETON("native_reflect_skeleton.h"),
        NATIVE_REFLECT_CONSTRUCTORS("native_reflect_constructors.h"),
        NATIVE_REFLECT_FIELD_ID_DECLARE("native_reflect_field_id_declare.h"),
        NATIVE_REFLECT_FIELD_ID_INIT("native_reflect_field_id_init.h"),
        NATIVE_REFLECT_FIELDS_GETTER_SETTER("native_reflect_fields_getter_setters.h"),
        NATIVE_REFLECT_FIELDS_GETTER("native_reflect_fields_getter.h"),
        NATIVE_REFLECT_FIELDS_SETTER("native_reflect_fields_setter.h"),
        NATIVE_REFLECT_FIELDS_GETTER_RETURN("native_reflect_fields_getter_return.h"),
        NATIVE_REFLECT_METHOD_ID_DECLARE("native_reflect_method_id_declare.h"),
        NATIVE_REFLECT_METHOD_ID_INIT("native_reflect_method_id_init.h"),
        NATIVE_REFLECT_METHODS("native_reflect_methods.h"),
        NATIVE_REFLECT_METHOD_RETURN("native_reflect_method_return.h"),
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
