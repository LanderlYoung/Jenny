package com.young.jenny;

import java.util.HashMap;

/**
 * Author: taylorcyang@tencent.com
 * Date:   2016-06-19
 * Time:   21:51
 * Life with Passion, Code with Creativity.
 */

public class Main {
    public static void main(String[] args) {
        ComputeInNative engine = new ComputeInNative();
        engine.init();
        engine.setParam(new HashMap<>());
        engine.request("{req:0}", ((success, rsp) -> {
            System.out.println("success=" + success + ", rsp=" + rsp);
        }));
        engine.release();
    }
}
