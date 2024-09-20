package io.github.landerlyoung.jennysampleapp

import android.graphics.*
import android.graphics.drawable.Drawable
import android.content.Context
import android.os.BatteryManager
import android.os.Bundle
import android.os.PowerManager
import android.telephony.TelephonyManager;

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
/*
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

    @Deprecated(
            message = "Moved to extension function. Put the 'content' argument first to fix Java",
            replaceWith = ReplaceWith(
                    expression = "content.toResponseBody(contentType)",
                    imports = ["okhttp3.ResponseBody.Companion.toResponseBody"]
            ),
            level = DeprecationLevel.WARNING)
    fun dep() {}

    internal fun it() {

    }
    @NativeProxyForClasses(namespace = "android::os", classes = [Context::class, PowerManager::class, BatteryManager::class, PowerManager.WakeLock::class, TelephonyManager::class])
    private val dummy = 0

    @NativeProxyForClasses(namespace = "java::lang", classes = [String::class])
    private val dummy2 = 0
    companion object {

        init {
            System.loadLibrary("hello-jenny")
        }
    }
}

@NativeProxy(namespace = "jenny", allMethods = true)
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
