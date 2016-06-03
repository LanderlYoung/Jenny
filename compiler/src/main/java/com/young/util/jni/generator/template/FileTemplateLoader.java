package com.young.util.jni.generator.template;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.CharBuffer;
import java.util.HashMap;
import java.util.Map;

/**
 * Author: taylorcyang@tencent.com
 * Date:   2016-06-03
 * Time:   00:26
 * Life with Passion, Code with Creativity.
 */
public class FileTemplateLoader {
    public String mDirectory;

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
