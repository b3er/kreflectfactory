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
internal fun <K, V, R : Map<K, V>> KType.newMap(
    size: Int,
    skipDefaults: Boolean = false,
    listsRange: IntRange = 0..10,
    mapsRange: IntRange = 0..10,
    numberRange: LongRange = Long.MIN_VALUE..Long.MAX_VALUE
): R {
    val keyType = arguments.getOrNull(0)?.type
        ?: throw IllegalArgumentException("Can't resolve key type ${arguments.firstOrNull()} for map $this")
    val valueType = arguments.getOrNull(1)?.type
        ?: throw IllegalArgumentException("Can't resolve value type ${arguments.firstOrNull()} for map $this")

    val map = newObjectsMap(
        keyType.newObjects<K>(size, skipDefaults, listsRange, mapsRange, numberRange),
        valueType.newObjects<V>(size, skipDefaults, listsRange, mapsRange, numberRange)
    )

    return when (this.classifier) {
        Map::class, MutableMap::class, AbstractMap::class, HashMap::class, LinkedHashMap::class -> map
        else -> {
            val mapClass = (classifier as KClass<*>)
            mapClass.constructors.find {
                it.parameters.size == 1 && it.parameters.first().type.isSubtypeOf(typeOf<Map<K, V>>())
            }?.call(map) ?: throw IllegalStateException("Can't find constructor with map argument for type $this")
        }
    } as R
}

private fun <K, V> newObjectsMap(keys: List<K>, values: List<V>): Map<K, V> {
    val result = LinkedHashMap<K, V>(keys.size)
    repeat(keys.size) {
        result[keys[it]] = values[it]
    }
    return result
}
