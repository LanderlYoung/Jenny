// Copyright (C) 2024 The Qt Company Ltd.

package io.github.landerlyoung.jenny

data class MethodIdDeclaration(
    val helper: HandyHelper,
    val listOfMethods: List<MethodOverloadResolver.MethodRecord>
)