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
import java.math.BigDecimal
import java.time.Clock
import java.time.Duration
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.OffsetDateTime
import java.time.Period
import java.time.ZoneId
import java.time.ZonedDateTime
import java.util.UUID
import kotlin.reflect.KParameter
import kotlin.reflect.KType

data class FixedObjectGenerator(
    val listSize: Int = 0,
    val boolean: Boolean = false,
    val char: Char = 'a',
    val int: Int = 0,
    val short: Short = int.toShort(),
    val long: Long = int.toLong(),
    val float: Float = int.toFloat(),
    val double: Double = int.toDouble(),
    val decimal: BigDecimal = int.toBigDecimal(),
    val string: String = "string",
    val uuid: UUID = UUID(0, 0),
    val clock: Clock = Clock.fixed(Instant.EPOCH, ZoneId.systemDefault()),
    val overrides: List<Any> = emptyList()
) : ObjectGenerator {
    override fun generate(type: KType): Any = with(type) {
        val classifier = type.classifier
        val override = overrides.find {
            it::class == classifier
        }
        when {
            override != null -> override
            isTypeOf<Boolean>() -> boolean
            isTypeOf<Char>() -> char
            isTypeOf<Short>() -> short
            isTypeOf<Int>() -> int
            isTypeOf<Long>() -> long
            isTypeOf<Float>() -> float
            isTypeOf<Double>() -> double
            isTypeOf<BigDecimal>() -> decimal
            isTypeOf<String>() -> string
            isTypeOf<UUID>() -> uuid
            isTypeOf<OffsetDateTime>() -> OffsetDateTime.ofInstant(clock.instant(), clock.zone)
            isTypeOf<ZonedDateTime>() -> ZonedDateTime.ofInstant(clock.instant(), clock.zone)
            isTypeOf<LocalDateTime>() -> LocalDateTime.ofInstant(clock.instant(), clock.zone)
            isTypeOf<LocalDate>() -> LocalDateTime.ofInstant(clock.instant(), clock.zone).toLocalDate()
            isTypeOf<LocalTime>() -> LocalDateTime.ofInstant(clock.instant(), clock.zone).toLocalTime()
            isTypeOf<Period>() -> Period.ofDays(int)
            isTypeOf<Duration>() -> Duration.ofMillis(long)
            isSubTypeOf<Enum<*>>() -> enumConstants().firstOrNull()
            else -> super.generate(type)
        }
    } as Any

    override fun generate(parameter: KParameter): Any {
        @Suppress("USELESS_CAST")
        return when {
            parameter.type.isTypeOf<String>() -> (parameter.name ?: string) as Any
            else -> super.generate(parameter)
        }
    }

    override fun generateSequence(type: KType): Sequence<*> = generateSequence { generate(type) }.take(listSize)
}

