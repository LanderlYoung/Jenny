package io.github.landerlyoung.jenny

/*
 * ```
 * Author: taylorcyang@tencent.com
 * Date:   2020-10-21
 * Time:   15:48
 * Life with Passion, Code with Creativity.
 * ```
 */

class FusionProxyGenerator(private val env: Environment, private val proxyClasses: Collection<CppClass>) {
    fun generate() {
        env.createOutputFile(Constants.JENNY_GEN_DIR_PROXY, env.configurations.fusionProxyHeaderName).use { out ->
            buildString {
                generateSourceContent()
            }.let { content ->
                out.write(content.toByteArray(Charsets.UTF_8))
            }
        }
    }

    private fun StringBuilder.generateSourceContent() {
        append(Constants.AUTO_GENERATE_NOTICE)
        append("""
                |#pragma once
                |
                |#include <jni.h>
                |
                |""".trimMargin())

        proxyClasses.forEach {
            append("""
                |#include "${it.headerFileName}"
                |
            """.trimMargin())
        }

        append("""
            |
            |namespace jenny {
            |
            |inline bool initAllProxies(JNIEnv* env) {
            |
            |   bool success = 
        """.trimMargin())

        append(
        proxyClasses.joinToString("&&\n") {
            it.namespace + "::" +it.name + "::initClazz(env)"
        })
        append(";")

        append("""
            |
            |   return success;
            |}
            |
            |} // end of namespace jenny
            |
        """.trimMargin())
    }
}