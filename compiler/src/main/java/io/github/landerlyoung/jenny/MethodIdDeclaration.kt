package io.github.landerlyoung.jenny

data class MethodIdDeclaration(
    val helper: HandyHelper,
    val listOfMethods: List<MethodOverloadResolver.MethodRecord>
)