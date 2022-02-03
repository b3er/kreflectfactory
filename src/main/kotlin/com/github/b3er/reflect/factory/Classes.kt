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

import kotlin.reflect.KClass

internal fun <T : Any> newClassObject(
    cls: KClass<T>,
    skipDefaults: Boolean = false,
    listsRange: IntRange = 0..10,
    mapsRange: IntRange = 0..10,
    numberRange: LongRange = Long.MIN_VALUE..Long.MAX_VALUE,
    reduce: T.() -> T = { this }
): T {
    val primaryConstructor = cls.constructors.first()
    val args = primaryConstructor.parameters
        .asSequence()
        .filterNot { skipDefaults && it.isOptional }
        .associateBy({ it }) {
            it.type.newObject<T>(skipDefaults, listsRange, mapsRange, numberRange)
        }
    return primaryConstructor.callBy(args).let(reduce)
}
