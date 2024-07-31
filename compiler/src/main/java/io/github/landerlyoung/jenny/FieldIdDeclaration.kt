package io.github.landerlyoung.jenny

import javax.lang.model.element.VariableElement


data class FieldIdDeclaration(
    val helper: HandyHelper,
    val listOfFields: List<VariableElement>
)
