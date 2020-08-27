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
        val threadSafe: Boolean = true,
        val errorLoggerFunction: String?,
        val outputDirectory: String?,
        val fusionProxyHeaderName: String,
        val headerOnlyProxy: Boolean = true,
        val useJniHelper: Boolean = false
) {
    companion object {
        private const val PREFIX = "jenny."

        val THREAD_SAFE = PREFIX + Configurations::threadSafe.name

        /**
         * external error log function
         * void (function_type)(JNIEnv* env, const char* error);
         */
        val ERROR_LOGGER_FUNCTION = PREFIX + Configurations::errorLoggerFunction.name

        val OUTPUT_DIRECTORY = PREFIX + Configurations::outputDirectory.name

        val FUSION_PROXY_HEADER_NAME = PREFIX + Configurations::fusionProxyHeaderName.name

        val HEADER_ONLY_PROXY = PREFIX + Configurations::headerOnlyProxy.name

        val USE_JNI_HELPER = PREFIX + Configurations::useJniHelper.name

        val ALL_OPTIONS = setOf(
                THREAD_SAFE,
                ERROR_LOGGER_FUNCTION,
                OUTPUT_DIRECTORY,
                FUSION_PROXY_HEADER_NAME,
                HEADER_ONLY_PROXY,
                USE_JNI_HELPER
        )

        fun fromOptions(options: Map<String, String>) = Configurations(
                options[THREAD_SAFE] != false.toString(),
                options[ERROR_LOGGER_FUNCTION],
                options[OUTPUT_DIRECTORY],
                options[FUSION_PROXY_HEADER_NAME] ?: Constants.JENNY_FUSION_PROXY_HEADER_NAME,
                options[HEADER_ONLY_PROXY] != false.toString(),
                options[USE_JNI_HELPER] == true.toString()
        )

        @JvmStatic
        fun main(args: Array<String>) {
            ALL_OPTIONS.forEach { println(it) }
        }
    }
}