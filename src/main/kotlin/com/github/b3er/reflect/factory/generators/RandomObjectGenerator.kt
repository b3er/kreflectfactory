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
package com.github.b3er.reflect.factory.generators

import com.github.b3er.reflect.factory.ObjectGenerator
import com.github.b3er.reflect.factory.ObjectGenerator.Companion.enumConstants
import com.github.b3er.reflect.factory.ObjectGenerator.Companion.isSubTypeOf
import com.github.b3er.reflect.factory.ObjectGenerator.Companion.isTypeOf
import com.github.b3er.reflect.factory.RandomClock
import com.github.b3er.reflect.factory.newBigDecimal
import com.github.b3er.reflect.factory.newBoolean
import com.github.b3er.reflect.factory.newDouble
import com.github.b3er.reflect.factory.newFloat
import com.github.b3er.reflect.factory.newInt
import com.github.b3er.reflect.factory.newLong
import com.github.b3er.reflect.factory.newString
import com.github.b3er.reflect.factory.newUUID
import java.math.BigDecimal
import java.time.Clock
import java.time.Duration
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.OffsetDateTime
import java.time.Period
import java.time.ZonedDateTime
import java.util.UUID
import kotlin.random.Random
import kotlin.reflect.KType

data class RandomObjectGenerator(
    val listsRange: IntRange = 0..10,
    val stringRange: IntRange = 1..255,
    val decimalRange: ClosedFloatingPointRange<Double> = Double.MIN_VALUE..Double.MAX_VALUE,
    val numberRange: LongRange = decimalRange.start.coerceAtLeast(Long.MIN_VALUE.toDouble())
        .toLong()..decimalRange.endInclusive.coerceAtMost(Long.MAX_VALUE.toDouble()).toLong(),
    val clock: Clock = RandomClock.SYSTEM_DEFAULT,
    val random: Random = Random,
) : ObjectGenerator {
    override fun generate(type: KType): Any = with(type) {
        when {
            isTypeOf<Boolean>() -> newBoolean(random)
            isTypeOf<Char>() -> newInt(numberRange, random).toChar()
            isTypeOf<Short>() -> newInt(numberRange, random).toShort()
            isTypeOf<Int>() -> newInt(numberRange, random)
            isTypeOf<Long>() -> newLong(numberRange, random)
            isTypeOf<Float>() -> newFloat(decimalRange, random)
            isTypeOf<Double>() -> newDouble(decimalRange, random)
            isTypeOf<BigDecimal>() -> newBigDecimal(decimalRange, random)
            isTypeOf<String>() -> newString(stringRange, random)
            isTypeOf<UUID>() -> newUUID(random)
            isTypeOf<OffsetDateTime>() -> OffsetDateTime.ofInstant(clock.instant(), clock.zone)
            isTypeOf<ZonedDateTime>() -> ZonedDateTime.ofInstant(clock.instant(), clock.zone)
            isTypeOf<LocalDateTime>() -> LocalDateTime.ofInstant(clock.instant(), clock.zone)
            isTypeOf<LocalDate>() -> LocalDateTime.ofInstant(clock.instant(), clock.zone).toLocalDate()
            isTypeOf<LocalTime>() -> LocalDateTime.ofInstant(clock.instant(), clock.zone).toLocalTime()
            isTypeOf<Period>() -> Period.ofDays(newInt(numberRange, random))
            isTypeOf<Duration>() -> Duration.ofMillis(newLong(numberRange, random))
            isSubTypeOf<Enum<*>>() -> enumConstants().firstOrNull()
            else -> super.generate(type)
        }
    } as Any

    override fun generateSequence(type: KType): Sequence<*> =
        generateSequence { generate(type) }.take(listsRange.random(random))

    companion object {
        val Default = RandomObjectGenerator()
    }
}
