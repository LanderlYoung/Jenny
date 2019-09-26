package io.github.landerlyoung.jenny

/*
 * ```
 * Author: taylorcyang@tencent.com
 * Date:   2019-09-26
 * Time:   10:07
 * Life with Passion, Code with Creativity.
 * ```
 */

data class Configurations(
        val cppNameSpace: String = "",
        val dynamicRegisterJniMethods: Boolean = true,
        val threadSafe: Boolean = true
) {
    companion object {
        private const val PREFIX = "Jenny."
        val CPP_NAME_SPACE = PREFIX + Configurations::cppNameSpace.name
        val DYNAMIC_REGISTER_JNI_METHOD = PREFIX + Configurations::dynamicRegisterJniMethods.name
        val THREAD_SAFE = PREFIX + Configurations::threadSafe.name

        fun fromOptions(options: Map<String, String>) = Configurations()
    }

}