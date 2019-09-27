package io.github.landerlyoung.jenny

/*
 * ```
 * Author: taylorcyang@tencent.com
 * Date:   2019-09-27
 * Time:   11:17
 * Life with Passion, Code with Creativity.
 * ```
 */
class NamespaceHelper(namespace: String) {
    private val namesSegment: List<String> = namespace.split("::").map { it.trim() }
    private val namespace: String = namesSegment.joinToString("::")

    val fileNamePrefix: String = namesSegment.joinToString("_").let {
        if (it.isNotEmpty()) {
            it + "_"
        } else {
            it
        }
    }

    fun beginNamespace() =
            if (namespace.isNotEmpty()) {
                // C++17 style
                "namespace $namespace {"
            } else {
                ""
            }

    fun endNamespace() =
            if (namespace.isNotEmpty())
                "} // endof namespace $namespace"
            else
                ""
}