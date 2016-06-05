package com.young.util.jni.generator.template;

import org.apache.commons.lang3.text.StrSubstitutor;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Time;
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
        JNI_CPP_TEMPLATE("jni_cpp_template.cpp"),
        JNI_HEADER_TEMPLATE("jni_header_template.h"),
        JNINATIVEMETHOD_STRUCT_TEMPLATE("JNINativeMethodStruct_template.cpp"),
        NATIVE_METHOD_DECLARE_TEMPLATE("native_method_declare_template.h"),
        NATIVE_METHOD_TEMPLATE("native_method_template.cpp"),
        CONSTANT_TEMPLATE("constant_template.h");

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
            StringBuilder sb = new StringBuilder();
            if (in != null) {
                BufferedReader bin = new BufferedReader(new InputStreamReader(in));
                String line;
                try {
                    while ((line = bin.readLine()) != null) {
                        sb.append(line);
                        sb.append('\n');
                    }
                    if (sb.length() > 0) {
                        sb.replace(sb.length() - 1, sb.length(), "");
                    }
                } catch (IOException e) {
                    try {
                        in.close();
                    } catch (IOException e1) {
                        //ignore
                    }
                }
            }
            return sb.toString();
        }
    }
}
