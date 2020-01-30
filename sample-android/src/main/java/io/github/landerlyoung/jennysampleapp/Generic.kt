package io.github.landerlyoung.jennysampleapp

import io.github.landerlyoung.jenny.NativeProxy
import java.io.Closeable
import java.util.concurrent.FutureTask

/*
 * ```
 * Author: taylorcyang@tencent.com
 * Date:   2020-01-30
 * Time:   19:18
 * Life with Passion, Code with Creativity.
 * ```
 */

@NativeProxy
class Generic<T : Runnable> {
    fun getAndRet(t: T): T = t

    fun <R> genericParam(r: R) {}

    fun <R> genericParamMultiUpperBounds(r: R) where R : Runnable, R : Closeable {
        genericParamMultiUpperBounds(r)
    }

    fun genericParam(t: Generic<Runnable>) {}

    fun genericParam2(t: Generic<FutureTask<Any>>) {}

    fun <R : Runnable> genericParam3(t: Generic<R>) {}

    fun genericParam4(t: Collection<out Runnable>) {}

    fun array(ia: IntArray) {}

    fun array(ia: Array<IntArray>) {}

}