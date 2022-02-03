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
import kotlin.reflect.KType
import kotlin.reflect.full.isSubtypeOf
import kotlin.reflect.typeOf

@Suppress("UNCHECKED_CAST")
internal fun <T, R : List<T>> KType.newList(
    size: Int, skipDefaults: Boolean = false,
    listsRange: IntRange = 0..10,
    mapsRange: IntRange = 0..10,
    numberRange: LongRange = Long.MIN_VALUE..Long.MAX_VALUE,
    reduce: T.() -> T = { this }
): R {
    val listType = arguments.firstOrNull()?.type
        ?: throw IllegalArgumentException("Can't resolve type ${arguments.firstOrNull()} for list $this")

    val list = listType.newObjects(size, skipDefaults, listsRange, mapsRange, numberRange, reduce)

    return when (this.classifier) {
        List::class, MutableList::class, Collection::class, Iterable::class -> list
        else -> {
            val listClass = (classifier as KClass<*>)
            listClass.constructors
                .find { it.parameters.size == 1 && it.parameters.first().type.isSubtypeOf(typeOf<Collection<*>>()) }
                ?.call(list)
                ?: throw IllegalStateException("Can't find constructor with collection argument for type $this")
        }
    } as R
}
