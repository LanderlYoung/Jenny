package io.github.landerlyoung.jennysampleapp

import io.github.landerlyoung.jenny.NativeMethodProxy
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

@NativeProxy(allMethods = true)
class Generic<T : Runnable> {
    fun getAndRet(t: T): T = t

    fun <R> genericParam(r: R) {
        unused(r)
    }

    fun <R> genericParamMultiUpperBounds(r: R) where R : Runnable, R : Closeable {
        genericParamMultiUpperBounds(r)
    }

    fun genericParam(t: Generic<Runnable>) {
        unused(t)
    }

    fun genericParam2(t: Generic<FutureTask<Any>>) {
        unused(t)
    }

    fun <R : Runnable> genericParam3(t: Generic<R>) {
        unused(t)
    }

    fun genericParam4(t: Collection<Runnable>) {
        unused(t)
    }

    fun array(ia: IntArray) {
        unused(ia)
    }

    fun array(ia: Array<IntArray>) {
        unused(ia)
    }

    @NativeMethodProxy(enabled = false)
    private fun unused(@Suppress("UNUSED") a: Any?) {
        a != null
    }
}
