/*
 * Copyright (C) 2022 Ilya Usanov
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * https://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.github.b3er.reflect.factory

import kotlin.random.Random
import kotlin.reflect.KClass


/**
 * Create new random enum value picked from list
 * @param values values to select random enum value from
 */
fun <T : Enum<T>> newEnum(random: Random, vararg values: T): T {
    return values[random.nextInt(values.size)]
}

/**
 * Create new random enum value picked from list
 * @param values values to select random enum value from
 */
fun <T : Enum<*>> newEnum(values: List<T>, random: Random): T {
    return values[random.nextInt(values.size)]
}

/**
 * Create new random enum value
 */
inline fun <reified T : Enum<T>> newEnum(random: Random): T = newEnum(T::class, random)

@PublishedApi
internal fun <T : Enum<T>> newEnum(cls: KClass<T>, random: Random): T {
    return newEnum(random, *cls.java.enumConstants)
}
