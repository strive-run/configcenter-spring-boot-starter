/*
 * Copyright 2022 Apollo Authors
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
 *
 */
package run.strive.configcenter.property

import org.springframework.beans.factory.config.BeanDefinition
import org.springframework.beans.factory.config.BeanExpressionContext
import org.springframework.beans.factory.config.ConfigurableBeanFactory
import org.springframework.util.StringUtils
import java.util.*

class PlaceholderHelper {
    /**
     * Resolve placeholder property values, e.g.
     * <br></br>
     * <br></br>
     * "${somePropertyValue}" -> "the actual property value"
     */
    fun resolvePropertyValue(beanFactory: ConfigurableBeanFactory, beanName: String?, placeholder: String?): Any {
        // resolve string value
        val strVal = beanFactory.resolveEmbeddedValue(placeholder)
        val bd = if (beanFactory.containsBean(beanName)) beanFactory
            .getMergedBeanDefinition(beanName) else null

        // resolve expressions like "#{systemProperties.myProp}"
        return evaluateBeanDefinitionString(beanFactory, strVal, bd)
    }

    private fun evaluateBeanDefinitionString(
        beanFactory: ConfigurableBeanFactory, value: String,
        beanDefinition: BeanDefinition?
    ): Any {
        if (beanFactory.beanExpressionResolver == null) {
            return value
        }
        val scope = if (beanDefinition != null) beanFactory
            .getRegisteredScope(beanDefinition.scope) else null
        return beanFactory.beanExpressionResolver
            .evaluate(value, BeanExpressionContext(beanFactory, scope))
    }

    /**
     * Extract keys from placeholder, e.g.
     *
     *  * ${some.key} => "some.key"
     *  * ${some.key:${some.other.key:100}} => "some.key", "some.other.key"
     *  * ${${some.key}} => "some.key"
     *  * ${${some.key:other.key}} => "some.key"
     *  * ${${some.key}:${another.key}} => "some.key", "another.key"
     *  * #{new java.text.SimpleDateFormat('${some.key}').parse('${another.key}')} => "some.key", "another.key"
     *
     */
    fun extractPlaceholderKeys(propertyString: String): Set<String?> {
        val placeholderKeys: MutableSet<String?> = HashSet()
        if (!StringUtils.hasLength(propertyString) || !isNormalizedPlaceholder(propertyString) && !isExpressionWithPlaceholder(
                propertyString
            )
        ) {
            return placeholderKeys
        }
        val stack = Stack<String?>()
        stack.push(propertyString)
        while (stack.isNotEmpty()) {
            val strVal = stack.pop()
            val startIndex = strVal!!.indexOf(PLACEHOLDER_PREFIX)
            if (startIndex == -1) {
                placeholderKeys.add(strVal)
                continue
            }
            val endIndex = findPlaceholderEndIndex(strVal, startIndex)
            if (endIndex == -1) {
                // invalid placeholder?
                continue
            }
            val placeholderCandidate = strVal.substring(startIndex + PLACEHOLDER_PREFIX.length, endIndex)

            // ${some.key:other.key}
            if (placeholderCandidate.startsWith(PLACEHOLDER_PREFIX)) {
                stack.push(placeholderCandidate)
            } else {
                // some.key:${some.other.key:100}
                val separatorIndex = placeholderCandidate.indexOf(VALUE_SEPARATOR)
                if (separatorIndex == -1) {
                    stack.push(placeholderCandidate)
                } else {
                    stack.push(placeholderCandidate.substring(0, separatorIndex))
                    val defaultValuePart = normalizeToPlaceholder(placeholderCandidate.substring(separatorIndex + VALUE_SEPARATOR.length))
                    if (StringUtils.hasLength(defaultValuePart)) {
                        stack.push(defaultValuePart)
                    }
                }
            }

            // has remaining part, e.g. ${a}.${b}
            if (endIndex + PLACEHOLDER_SUFFIX.length < strVal.length - 1) {
                val remainingPart = normalizeToPlaceholder(strVal.substring(endIndex + PLACEHOLDER_SUFFIX.length))
                if (StringUtils.hasLength(remainingPart)) {
                    stack.push(remainingPart)
                }
            }
        }
        return placeholderKeys
    }

    private fun isNormalizedPlaceholder(propertyString: String): Boolean {
        return propertyString.startsWith(PLACEHOLDER_PREFIX) && propertyString.contains(PLACEHOLDER_SUFFIX)
    }

    private fun isExpressionWithPlaceholder(propertyString: String): Boolean {
        return (propertyString.startsWith(EXPRESSION_PREFIX) && propertyString.contains(EXPRESSION_SUFFIX)
                && propertyString.contains(PLACEHOLDER_PREFIX) && propertyString.contains(PLACEHOLDER_SUFFIX))
    }

    private fun normalizeToPlaceholder(strVal: String): String? {
        val startIndex = strVal.indexOf(PLACEHOLDER_PREFIX)
        if (startIndex == -1) {
            return null
        }
        val endIndex = strVal.lastIndexOf(PLACEHOLDER_SUFFIX)
        return if (endIndex == -1) {
            null
        } else strVal.substring(startIndex, endIndex + PLACEHOLDER_SUFFIX.length)
    }

    private fun findPlaceholderEndIndex(buf: CharSequence?, startIndex: Int): Int {
        var index = startIndex + PLACEHOLDER_PREFIX.length
        var withinNestedPlaceholder = 0
        while (index < buf!!.length) {
            if (StringUtils.substringMatch(buf, index, PLACEHOLDER_SUFFIX)) {
                index = if (withinNestedPlaceholder > 0) {
                    withinNestedPlaceholder--
                    index + PLACEHOLDER_SUFFIX.length
                } else {
                    return index
                }
            } else if (StringUtils.substringMatch(buf, index, SIMPLE_PLACEHOLDER_PREFIX)) {
                withinNestedPlaceholder++
                index = index + SIMPLE_PLACEHOLDER_PREFIX.length
            } else {
                index++
            }
        }
        return -1
    }

    companion object {
        private const val PLACEHOLDER_PREFIX = "\${"
        private const val PLACEHOLDER_SUFFIX = "}"
        private const val VALUE_SEPARATOR = ":"
        private const val SIMPLE_PLACEHOLDER_PREFIX = "{"
        private const val EXPRESSION_PREFIX = "#{"
        private const val EXPRESSION_SUFFIX = "}"
    }
}
