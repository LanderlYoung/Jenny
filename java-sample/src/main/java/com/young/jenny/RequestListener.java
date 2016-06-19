package com.young.jenny;

import com.young.jenny.annotation.NativeProxy;

/**
 * Author: taylorcyang@tencent.com
 * Date:   2016-06-19
 * Time:   21:54
 * Life with Passion, Code with Creativity.
 */
@NativeProxy
public interface RequestListener {
    void onResponse(boolean success, String rsp);
}
