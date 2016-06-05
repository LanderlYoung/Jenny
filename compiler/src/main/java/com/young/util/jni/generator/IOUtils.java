package com.young.util.jni.generator;

import java.io.Closeable;
import java.io.IOException;

/**
 * Author: landerlyoung@gmail.com
 * Date:   2016-06-05
 * Time:   17:49
 * Life with Passion, Code with Creativity.
 */
public class IOUtils {
    public static void closeSilently(Closeable c) {
        if (c == null) return;
        try {
            c.close();
        } catch (IOException e) {

        }
    }
}
