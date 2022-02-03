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

import java.math.BigDecimal
import java.util.*
import kotlin.reflect.KClass
import kotlin.reflect.KType
import kotlin.reflect.full.isSubtypeOf
import kotlin.reflect.typeOf


/**
 * Create an object of specified type
 * @param skipDefaults skip randomizing of parameters with default values
 * @param collectionRange lists and maps size range for parameters
 * @param numberRange number values range for parameters
 * @param reduce function to reduce created object
 */
inline fun <reified T> newObject(
    skipDefaults: Boolean = false,
    collectionRange: IntRange = 0..10,
    numberRange: LongRange = Long.MIN_VALUE..Long.MAX_VALUE,
    noinline reduce: T.() -> T = { this }
): T {
    return typeOf<T>().newObject(skipDefaults, collectionRange, collectionRange, numberRange, reduce)
}

/**
 * Create an object of specified type
 * @param skipDefaults skip randomizing of parameters with default values
 * @param listsRange lists size range for parameters
 * @param mapsRange maps size range for parameters
 * @param numberRange number values range for parameters
 * @param reduce function to reduce created object
 */
inline fun <reified T> newObject(
    skipDefaults: Boolean = false,
    listsRange: IntRange = 0..10,
    mapsRange: IntRange = 0..10,
    numberRange: LongRange = Long.MIN_VALUE..Long.MAX_VALUE,
    noinline reduce: T.() -> T = { this }
): T {
    return typeOf<T>().newObject(skipDefaults, listsRange, mapsRange, numberRange, reduce)
}

/**
 * Create a list of objects of specified type
 * @param size desired count of objects ibn returned list
 * @param skipDefaults skip randomizing of parameters with default values
 * @param collectionRange lists and maps size range for parameters
 * @param numberRange number values range for parameters
 * @param reduce function to reduce created object
 */
inline fun <reified T> newObjects(
    size: Int,
    skipDefaults: Boolean = false,
    collectionRange: IntRange = 0..10,
    numberRange: LongRange = Long.MIN_VALUE..Long.MAX_VALUE,
    noinline reduce: T.() -> T = { this }
): List<T> {
    return typeOf<T>().newObjects(size, skipDefaults, collectionRange, collectionRange, numberRange, reduce)
}


/**
 * Create a list of objects of specified type
 * @param size desired count of objects ibn returned list
 * @param skipDefaults skip randomizing of parameters with default values
 * @param listsRange lists size range for parameters
 * @param mapsRange maps size range for parameters
 * @param numberRange number values range for parameters
 * @param reduce function to reduce created object
 */
inline fun <reified T> newObjects(
    size: Int,
    skipDefaults: Boolean = false,
    listsRange: IntRange = 0..10,
    mapsRange: IntRange = 0..10,
    numberRange: LongRange = Long.MIN_VALUE..Long.MAX_VALUE,
    noinline reduce: T.() -> T = { this }
): List<T> {
    return typeOf<T>().newObjects(size, skipDefaults, listsRange, mapsRange, numberRange, reduce)
}

/**
 * Create a list of objects of specified type
 * @param size desired count of objects ibn returned list
 * @param skipDefaults skip randomizing of parameters with default values
 * @param listsRange lists size range for parameters
 * @param mapsRange maps size range for parameters
 * @param numberRange number values range for parameters
 * @param reduce function to reduce created object
 */
fun <T> KType.newObjects(
    size: Int,
    skipDefaults: Boolean = false,
    listsRange: IntRange = 0..10,
    mapsRange: IntRange = 0..10,
    numberRange: LongRange = Long.MIN_VALUE..Long.MAX_VALUE,
    reduce: T.() -> T = { this }
): List<T> {
    val result = ArrayList<T>(size)
    repeat(size) {
        result.add(newObject(skipDefaults, listsRange, mapsRange, numberRange, reduce))
    }
    return result
}

/**
 * Create an object of specified type
 * @param skipDefaults skip randomizing of parameters with default values
 * @param listsRange lists size range for parameters
 * @param mapsRange maps size range for parameters
 * @param numberRange number values range for parameters
 * @param reduce function to reduce created object
 */
@Suppress("UNCHECKED_CAST")
fun <T> KType.newObject(
    skipDefaults: Boolean = false,
    listsRange: IntRange = 0..10,
    mapsRange: IntRange = 0..10,
    numberRange: LongRange = Long.MIN_VALUE..Long.MAX_VALUE,
    reduce: T.() -> T = { this }
): T {
    @Suppress("IMPLICIT_CAST_TO_ANY", "TYPE_MISMATCH_WARNING")
    val result = when {
        this == typeOf<Boolean>() -> newBoolean()
        this == typeOf<Char>() -> newInt(numberRange).toChar()
        this == typeOf<Short>() -> newInt(numberRange).toShort()
        this == typeOf<Int>() -> newInt(numberRange)
        this == typeOf<Long>() -> newLong(numberRange)
        this == typeOf<Float>() -> newFloat(numberRange)
        this == typeOf<Double>() -> newDouble(numberRange)
        this == typeOf<BigDecimal>() -> newBigDecimal(numberRange)
        this == typeOf<String>() -> newString()
        this == typeOf<UUID>() -> UUID.randomUUID()
        this == typeOf<BooleanArray>() -> BooleanArray(listsRange.random()) { newBoolean() }
        this == typeOf<CharArray>() -> CharArray(listsRange.random()) { newInt(numberRange).toChar() }
        this == typeOf<ShortArray>() -> ShortArray(listsRange.random()) { newInt(numberRange).toShort() }
        this == typeOf<IntArray>() -> IntArray(listsRange.random()) { newInt(numberRange) }
        this == typeOf<LongArray>() -> LongArray(listsRange.random()) { newLong(numberRange) }
        this == typeOf<FloatArray>() -> FloatArray(listsRange.random()) { newFloat(numberRange) }
        this == typeOf<DoubleArray>() -> DoubleArray(listsRange.random()) { newDouble(numberRange) }
        isSubtypeOf(typeOf<Enum<*>>()) -> newEnum(classifier as KClass<out Enum<*>>)
        isSubtypeOf(typeOf<Collection<*>>()) -> {
            newList<Any, List<Any>>(listsRange.random(), skipDefaults, listsRange, mapsRange, numberRange)
        }
        isSubtypeOf(typeOf<Array<*>>()) -> {
            newList<Any, List<Any>>(
                listsRange.random(),
                skipDefaults,
                listsRange,
                mapsRange,
                numberRange
            ).toTypedArray()
        }
        isSubtypeOf(typeOf<Map<*, *>>()) -> {
            newMap<Any, Any, Map<Any, Any>>(mapsRange.random(), skipDefaults, listsRange, mapsRange, numberRange)
        }
        classifier is KClass<*> -> {
            newClassObject(classifier as KClass<*>, skipDefaults, listsRange, mapsRange, numberRange)
        }
        else -> {
            throw IllegalArgumentException("Type $this is not supported")
        }
    } as T
    return result.reduce()
}



