package io.github.landerlyoung.jennysampleapp

import android.graphics.Canvas
import android.graphics.ColorFilter
import android.graphics.PixelFormat
import android.graphics.drawable.Drawable
import io.github.landerlyoung.jenny.NativeClass
import io.github.landerlyoung.jenny.NativeFieldProxy
import io.github.landerlyoung.jenny.NativeProxy

/*
 * ```
 * Author: landerlyoung@gmail.com
 * Date:   2019-10-08
 * Time:   16:33
 * Life with Passion, Code with Creativity.
 * ```
 */
@NativeClass
@NativeProxy(allMethods = false, allFields = false)
class NativeDrawable : Drawable() {
    @NativeFieldProxy(setter = false)
    private val nativeHandle = nativeInit()

    private external fun nativeInit(): Long

    external fun onClick()

    external override fun draw(canvas: Canvas)

    external fun release()

    override fun setAlpha(alpha: Int) {
    }

    override fun getOpacity(): Int {
        return PixelFormat.OPAQUE
    }

    override fun setColorFilter(colorFilter: ColorFilter?) {
    }

    companion object {
        init {
            System.loadLibrary("hello-jenny")
        }
    }
}
