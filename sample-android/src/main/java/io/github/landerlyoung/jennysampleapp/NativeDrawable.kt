package io.github.landerlyoung.jennysampleapp

import android.graphics.*
import android.graphics.drawable.Drawable
import io.github.landerlyoung.jenny.NativeClass
import io.github.landerlyoung.jenny.NativeFieldProxy
import io.github.landerlyoung.jenny.NativeProxy
import io.github.landerlyoung.jenny.NativeProxyForClasses

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
@NativeProxyForClasses(namespace = "android",
        classes = [Paint.Style::class, Rect::class])
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

@NativeProxy(namespace = "jenny")
object Graphics {
    @JvmStatic
    fun newPaint() = Paint(Paint.ANTI_ALIAS_FLAG)

    @JvmStatic
    fun paintSetStyle(paint: Paint, style: Paint.Style) {
        paint.style = style
    }

    @JvmStatic
    fun drawableCircle(canvas: Canvas, x: Float, y: Float, r: Float, paint: Paint) {
        canvas.drawCircle(x, y, r, paint)
    }

    @JvmStatic
    fun drawableGetBounds(drawable: Drawable) = drawable.bounds

    @JvmStatic
    fun setColor(paint: Paint, color: Int) {
        paint.color = color
    }
}
