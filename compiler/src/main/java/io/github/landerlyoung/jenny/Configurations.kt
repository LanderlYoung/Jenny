/**
 * Copyright (C) 2024 The Qt Company Ltd.
 * Copyright 2016 landerlyoung@gmail.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
        val templateDirectory: String?,
        val templateBuildSuffix: String?,
        val fusionProxyHeaderName: String,
        val headerOnlyProxy: Boolean = true,
        val useJniHelper: Boolean = false,
        val useTemplates: Boolean = false
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

        val TEMPLATE_DIRECTORY = PREFIX + Configurations::templateDirectory.name

        val TEMPLATE_BUILD_SUFFIX = PREFIX + Configurations::templateBuildSuffix.name

        val FUSION_PROXY_HEADER_NAME = PREFIX + Configurations::fusionProxyHeaderName.name

        val HEADER_ONLY_PROXY = PREFIX + Configurations::headerOnlyProxy.name

        val USE_JNI_HELPER = PREFIX + Configurations::useJniHelper.name

        val USE_TEMPLATES = PREFIX + Configurations::useTemplates.name

        val ALL_OPTIONS = setOf(
                THREAD_SAFE,
                ERROR_LOGGER_FUNCTION,
                OUTPUT_DIRECTORY,
                TEMPLATE_DIRECTORY,
                TEMPLATE_BUILD_SUFFIX,
                FUSION_PROXY_HEADER_NAME,
                HEADER_ONLY_PROXY,
                USE_JNI_HELPER,
                USE_TEMPLATES
        )

        fun fromOptions(options: Map<String, String>) = Configurations(
                options[THREAD_SAFE] != false.toString(),
                options[ERROR_LOGGER_FUNCTION],
                options[OUTPUT_DIRECTORY],
                options[TEMPLATE_DIRECTORY],
                options[TEMPLATE_BUILD_SUFFIX],
                options[FUSION_PROXY_HEADER_NAME] ?: Constants.JENNY_FUSION_PROXY_HEADER_NAME,
                options[HEADER_ONLY_PROXY] != false.toString(),
                options[USE_JNI_HELPER] == true.toString(),
                options[USE_TEMPLATES] == true.toString()
        )

        @JvmStatic
        fun main(args: Array<String>) {
            ALL_OPTIONS.forEach { println(it) }
        }
    }
}