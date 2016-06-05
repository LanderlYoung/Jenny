package com.young.jennysampleapp;

import android.telecom.Call;

import com.young.jenny.annotation.NativeAccessField;
import com.young.jenny.annotation.NativeReflect;
import com.young.jenny.annotation.NativeReflectMethod;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Author: landerlyoung@gmail.com
 * Date:   2016-06-02
 * Time:   19-58
 * Life with Passion, Code with Creativity.
 */
@NativeReflect(allFields = false, allMethods = false)
public class Callback {

    protected Object lock;

    @NativeAccessField(getter = true, setter = true, auto = false)
    public int count;

    public static ArrayList<String> staticField;

    @NativeReflectMethod
    public Callback() {
    }

    public Callback(int a) {

    }

    public Callback(HashMap<?,?> sth) {

    }

    public static int aStaticMethod() {
        return 0;
    }

    @NativeReflectMethod
    void onJobDone(boolean success, String result) {

    }

    @NativeReflectMethod
    void onJobProgress(long progress) {

    }

    @NativeReflectMethod
    void onJobStart() {

    }

    int prepareRun() {
        return 0;
    }
}
