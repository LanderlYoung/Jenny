package io.github.landerlyoung.jenny

/*
 * ```
 * Author: landerlyoung@gmail.com
 * Date:   2019-09-27
 * Time:   11:17
 * Life with Passion, Code with Creativity.
 * ```
 */
class NamespaceHelper(namespace: String) {
    private val namespaces: List<String> = namespace.split("::").map { it.trim() }
            .filter { it.isNotEmpty() }

    val fileNamePrefix: String = namespaces.joinToString("_").let {
        if (it.isNotEmpty()) {
            it + "_"
        } else {
            it
        }
    }

    fun beginNamespace() = namespaces.joinToString(" ") { "namespace $it {" }

    fun endNamespace() = if (namespaces.isNotEmpty())
        namespaces.joinToString(" ", postfix = " // endof namespace ${namespaces.joinToString("::") { it }}") { "}" }
    else ""
}