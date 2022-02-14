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
import java.time.*
import java.util.*
import kotlin.reflect.KClass
import kotlin.reflect.KType
import kotlin.reflect.full.isSubtypeOf
import kotlin.reflect.full.withNullability
import kotlin.reflect.typeOf


/**
 * Create an object of specified type
 * @param skipDefaults skip randomizing of parameters with default values
 * @param collectionRange lists and maps size range for parameters
 * @param numberRange number values range for parameters
 * @param clock clock used to create date/time values
 * @param reduce function to reduce created object
 */
inline fun <reified T> newObject(
    skipDefaults: Boolean = false,
    collectionRange: IntRange = 0..10,
    numberRange: LongRange = Long.MIN_VALUE..Long.MAX_VALUE,
    clock: Clock = RandomClock.SYSTEM_DEFAULT,
    noinline reduce: T.() -> T = { this }
): T {
    return typeOf<T>().newObject(skipDefaults, collectionRange, collectionRange, numberRange, clock, reduce)
}

/**
 * Create an object of specified type
 * @param skipDefaults skip randomizing of parameters with default values
 * @param listsRange lists size range for parameters
 * @param mapsRange maps size range for parameters
 * @param numberRange number values range for parameters
 * @param clock clock used to create date/time values
 * @param reduce function to reduce created object
 */
inline fun <reified T> newObject(
    skipDefaults: Boolean = false,
    listsRange: IntRange = 0..10,
    mapsRange: IntRange = 0..10,
    numberRange: LongRange = Long.MIN_VALUE..Long.MAX_VALUE,
    clock: Clock = RandomClock.SYSTEM_DEFAULT,
    noinline reduce: T.() -> T = { this }
): T {
    return typeOf<T>().newObject(skipDefaults, listsRange, mapsRange, numberRange, clock, reduce)
}

/**
 * Create a list of objects of specified type
 * @param size desired count of objects ibn returned list
 * @param skipDefaults skip randomizing of parameters with default values
 * @param collectionRange lists and maps size range for parameters
 * @param numberRange number values range for parameters
 * @param clock clock used to create date/time values
 * @param reduce function to reduce created object
 */
inline fun <reified T> newObjects(
    size: Int,
    skipDefaults: Boolean = false,
    collectionRange: IntRange = 0..10,
    numberRange: LongRange = Long.MIN_VALUE..Long.MAX_VALUE,
    clock: Clock = RandomClock.SYSTEM_DEFAULT,
    noinline reduce: T.(index: Int) -> T = { this }
): List<T> {
    return typeOf<T>().newObjects(size, skipDefaults, collectionRange, collectionRange, numberRange, clock, reduce)
}


/**
 * Create a list of objects of specified type
 * @param size desired count of objects ibn returned list
 * @param skipDefaults skip randomizing of parameters with default values
 * @param listsRange lists size range for parameters
 * @param mapsRange maps size range for parameters
 * @param numberRange number values range for parameters
 * @param clock clock used to create date/time values
 * @param reduce function to reduce created object
 */
inline fun <reified T> newObjects(
    size: Int,
    skipDefaults: Boolean = false,
    listsRange: IntRange = 0..10,
    mapsRange: IntRange = 0..10,
    numberRange: LongRange = Long.MIN_VALUE..Long.MAX_VALUE,
    clock: Clock = RandomClock.SYSTEM_DEFAULT,
    noinline reduce: T.(index: Int) -> T = { this }
): List<T> {
    return typeOf<T>().newObjects(size, skipDefaults, listsRange, mapsRange, numberRange, clock, reduce)
}

/**
 * Create a list of objects of specified type
 * @param size desired count of objects ibn returned list
 * @param skipDefaults skip randomizing of parameters with default values
 * @param listsRange lists size range for parameters
 * @param mapsRange maps size range for parameters
 * @param numberRange number values range for parameters
 * @param clock clock used to create date/time values
 * @param reduce function to reduce created object
 */
fun <T> KType.newObjects(
    size: Int,
    skipDefaults: Boolean = false,
    listsRange: IntRange = 0..10,
    mapsRange: IntRange = 0..10,
    numberRange: LongRange = Long.MIN_VALUE..Long.MAX_VALUE,
    clock: Clock = RandomClock.SYSTEM_DEFAULT,
    reduce: T.(index: Int) -> T = { this }
): List<T> {
    val result = ArrayList<T>(size)
    repeat(size) {
        result.add(newObject(skipDefaults, listsRange, mapsRange, numberRange, clock) {
            reduce(it)
        })
    }
    return result
}

/**
 * Create an object of specified type
 * @param skipDefaults skip randomizing of parameters with default values
 * @param listsRange lists size range for parameters
 * @param mapsRange maps size range for parameters
 * @param numberRange number values range for parameters
 * @param clock clock used to create date/time values
 * @param reduce function to reduce created object
 */
@Suppress("UNCHECKED_CAST")
fun <T> KType.newObject(
    skipDefaults: Boolean = false,
    listsRange: IntRange = 0..10,
    mapsRange: IntRange = 0..10,
    numberRange: LongRange = Long.MIN_VALUE..Long.MAX_VALUE,
    clock: Clock = RandomClock.SYSTEM_DEFAULT,
    reduce: T.() -> T = { this }
): T {
    val nonNullType = withNullability(false)

    @Suppress("IMPLICIT_CAST_TO_ANY", "TYPE_MISMATCH_WARNING")
    val result = when {
        nonNullType == typeOf<Boolean>() -> newBoolean()
        nonNullType == typeOf<Char>() -> newInt(numberRange).toChar()
        nonNullType == typeOf<Short>() -> newInt(numberRange).toShort()
        nonNullType == typeOf<Int>() -> newInt(numberRange)
        nonNullType == typeOf<Long>() -> newLong(numberRange)
        nonNullType == typeOf<Float>() -> newFloat(numberRange)
        nonNullType == typeOf<Double>() -> newDouble(numberRange)
        nonNullType == typeOf<BigDecimal>() -> newBigDecimal(numberRange)
        nonNullType == typeOf<String>() -> newString()
        nonNullType == typeOf<UUID>() -> UUID.randomUUID()
        nonNullType == typeOf<BooleanArray>() -> BooleanArray(listsRange.random()) { newBoolean() }
        nonNullType == typeOf<CharArray>() -> CharArray(listsRange.random()) { newInt(numberRange).toChar() }
        nonNullType == typeOf<ShortArray>() -> ShortArray(listsRange.random()) { newInt(numberRange).toShort() }
        nonNullType == typeOf<IntArray>() -> IntArray(listsRange.random()) { newInt(numberRange) }
        nonNullType == typeOf<LongArray>() -> LongArray(listsRange.random()) { newLong(numberRange) }
        nonNullType == typeOf<FloatArray>() -> FloatArray(listsRange.random()) { newFloat(numberRange) }
        nonNullType == typeOf<DoubleArray>() -> DoubleArray(listsRange.random()) { newDouble(numberRange) }
        nonNullType == typeOf<OffsetDateTime>() -> OffsetDateTime.ofInstant(clock.instant(), clock.zone)
        nonNullType == typeOf<ZonedDateTime>() -> ZonedDateTime.ofInstant(clock.instant(), clock.zone)
        nonNullType == typeOf<LocalDateTime>() -> LocalDateTime.ofInstant(clock.instant(), clock.zone)
        nonNullType == typeOf<LocalDate>() -> LocalDate.ofInstant(clock.instant(), clock.zone)
        nonNullType == typeOf<LocalTime>() -> LocalTime.ofInstant(clock.instant(), clock.zone)
        nonNullType == typeOf<Period>() -> Period.ofDays(newInt(numberRange))
        nonNullType == typeOf<Duration>() -> Duration.ofMillis(newLong(numberRange))
        nonNullType.isSubtypeOf(typeOf<Enum<*>>()) -> newEnum(classifier as KClass<out Enum<*>>)
        nonNullType.isSubtypeOf(typeOf<Collection<*>>()) -> {
            newList<Any, List<Any>>(listsRange.random(), skipDefaults, listsRange, mapsRange, numberRange, clock)
        }
        nonNullType.isSubtypeOf(typeOf<Array<*>>()) -> {
            newList<Any, List<Any>>(
                listsRange.random(),
                skipDefaults,
                listsRange,
                mapsRange,
                numberRange,
                clock
            ).toTypedArray()
        }
        nonNullType.isSubtypeOf(typeOf<Map<*, *>>()) -> {
            newMap<Any, Any, Map<Any, Any>>(mapsRange.random(), skipDefaults, listsRange, mapsRange, numberRange, clock)
        }
        classifier is KClass<*> -> {
            newClassObject(classifier as KClass<*>, skipDefaults, listsRange, mapsRange, numberRange, clock)
        }
        else -> {
            throw IllegalArgumentException("Type $this is not supported")
        }
    } as T
    return result.reduce()
}



