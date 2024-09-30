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

import javax.lang.model.element.ExecutableElement

/*
 * ```
 * Author: landerlyoung@gmail.com
 * Date:   2019-09-26
 * Time:   17:26
 * Life with Passion, Code with Creativity.
 * ```
 */
class MethodOverloadResolver(
    private val helper: HandyHelper,
    private val nativeParamResolver: (ExecutableElement) -> String
) {
    data class MethodRecord(
        val method: ExecutableElement,
        val resolvedPostFix: String,
        val index: Int
    )

    fun resolve(methodList: List<ExecutableElement>): List<MethodRecord> {
        val duplicateRecord = mutableMapOf<String, Boolean>()
        methodList.forEach {
            val p = nativeParamResolver(it)
            duplicateRecord[p] = duplicateRecord.containsKey(p)
        }

        return methodList.mapIndexed { index, m ->
            val p = nativeParamResolver(m)
            if (duplicateRecord[p]!! || Constants.CPP_RESERVED_WORS.contains(m.simpleName.toString())) {
                MethodRecord(
                    m,
                    helper.getMethodOverloadPostfix(m),
                    index
                )
            } else {
                MethodRecord(m, "", index)
            }
        }
    }

}