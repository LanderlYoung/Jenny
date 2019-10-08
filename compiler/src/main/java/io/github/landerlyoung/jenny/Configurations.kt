package io.github.landerlyoung.jenny

/**
 * ```
 * Author: landerlyoung@gmail.com
 * Date:   2019-09-26
 * Time:   10:07
 * Life with Passion, Code with Creativity.
 * ```
 * 
 * 1. jenny.threadSafe: generate thread safe proxy code or not
 * 
 */

data class Configurations(
        val threadSafe: Boolean = true
) {
    companion object {
        private const val PREFIX = "jenny."
        val THREAD_SAFE = PREFIX + Configurations::threadSafe.name

        fun fromOptions(options: Map<String, String>) = Configurations(
                options[THREAD_SAFE] != false.toString()
        )
    }
}