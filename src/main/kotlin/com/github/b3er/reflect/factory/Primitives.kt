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
import java.util.UUID
import kotlin.random.Random


/**
 * @return a new boolean.
 * @param random used [Random] for generation.
 */
fun newBoolean(random: Random = Random): Boolean {
    return random.nextBoolean()
}

/**
 * @return a new integer.
 * @param random used [Random] for generation.
 */
fun newInt(random: Random = Random): Int {
    return random.nextInt()
}

/**
 * Given a min and a max, create a new integer.
 *
 * @param min the minimum value.
 * @param max the maximum value.
 * @param random used [Random] for generation.
 * @return a value between min and max.
 */
fun newInt(min: Int? = null, max: Int? = null, random: Random = Random): Int {
    val minValue = min ?: Int.MIN_VALUE
    val maxValue = max ?: Int.MAX_VALUE

    require(minValue <= maxValue) {
        "The minimum value ($min) cannot higher than the maximum value ($max)."
    }

    val intRange = maxValue - minValue + 1
    return if (intRange < 0) {
        minValue + random.nextInt(Int.MAX_VALUE)
    } else {
        minValue + random.nextInt(intRange)
    }
}

/**
 * Given a range, create a new integer.
 *
 * @param range the range to use when creating the integer.
 * @param random used [Random] for generation.
 * @return random integer within the given range.
 */
fun newInt(range: IntRange, random: Random = Random): Int {
    return range.random(random)
}

/**
 * Given a long range, create a new integer.
 *
 * @param range the range to use when creating the integer.
 * @param random used [Random] for generation.
 * @return random integer within the given range.
 */
fun newInt(range: LongRange, random: Random = Random): Int {
    return range.let {
        it.first.coerceAtLeast(Int.MIN_VALUE.toLong()).toInt()..it.last.coerceAtMost(Int.MAX_VALUE.toLong()).toInt()
    }.random(random)
}

/**
 * Given a min and a max, create a new long.
 *
 * @param min the minimum value.
 * @param max the maximum value.
 * @param random used [Random] for generation.
 * @return a value between min and max.
 */
fun newLong(min: Long? = null, max: Long? = null, random: Random = Random): Long {
    val minValue = min ?: Long.MIN_VALUE
    val maxValue = max ?: Long.MAX_VALUE

    if (minValue == Long.MIN_VALUE && maxValue == Long.MAX_VALUE) {
        return random.nextLong()
    }

    require(minValue <= maxValue) {
        "The minimum value ($min) cannot higher than the maximum value ($max)."
    }

    val longRange = maxValue - minValue + 1
    return if (longRange < 0) {
        minValue + (random.nextFloat() * Long.MAX_VALUE).toLong()
    } else {
        minValue + (random.nextFloat() * longRange).toLong()
    }
}

/**
 * Given a long range, create a new integer.
 *
 * @param range the range to use when creating the long.
 * @param random used [Random] for generation.
 * @return random long within the given range.
 */
fun newLong(range: LongRange, random: Random = Random): Long {
    return range.random(random)
}

/**
 * Creates a non-empty string that has a number of characters between the provided
 * range (defaults to 1-255).
 *
 * @param range the range representing the possible number of characters.
 * @param random used [Random] for generation.
 * @return the string
 */
fun newString(range: IntRange = 1..255, random: Random = Random): String {
    val characters = ('a'..'z') + ('A'..'Z') + ('0'..'9')
    return (1..newInt(range, random)).map { characters.random(random) }.joinToString("")
}

/**
 * Given a long range, create a new double.
 *
 * @param range the range to use when creating the double.
 * @param random used [Random] for generation.
 * @return random long within the given range.
 */
fun newDouble(range: ClosedFloatingPointRange<Double>, random: Random = Random): Double {
    return random.nextDouble(range.start, range.endInclusive)
}

/**
 * Given a long range, create a new double.
 *
 * @param range the range to use when creating the double.
 * @param random used [Random] for generation.
 * @return random long within the given range.
 */
fun newDouble(range: LongRange, random: Random = Random): Double {
    return random.nextDouble(range.first.toDouble(), range.last.toDouble())
}

/**
 * Given a range, create a new double.
 *
 * @param range the range to use when creating the double.
 * @param random used [Random] for generation.
 * @return random long within the given range.
 */
fun newDouble(range: IntRange, random: Random = Random): Double {
    return random.nextDouble(range.first.toDouble(), range.last.toDouble())
}

/**
 * Given a long range, create a new float.
 *
 * @param range the range to use when creating the long.
 * @param random used [Random] for generation.
 * @return random long within the given range.
 */
fun newFloat(range: LongRange, random: Random = Random): Float {
    return newFloat(range.let {
        it.first.coerceAtLeast(Int.MIN_VALUE.toLong()).toInt()..it.last.coerceAtMost(Int.MAX_VALUE.toLong()).toInt()
    }, random)
}

/**
 * Given a long range, create a new float.
 *
 * @param range the range to use when creating the long.
 * @param random used [Random] for generation.
 * @return random long within the given range.
 */
fun newFloat(range: ClosedFloatingPointRange<Double>, random: Random = Random): Float {
    return newFloat(range.let {
        it.start.coerceAtLeast(Float.MIN_VALUE.toDouble())..it.endInclusive.coerceAtMost(Float.MAX_VALUE.toDouble())
    }, random)
}

/**
 * Given a range, create a new float.
 *
 * @param range the range to use when creating the long.
 * @param random used [Random] for generation.
 * @return random long within the given range.
 */
fun newFloat(range: IntRange, random: Random = Random): Float {
    return random.nextFloat().let { it * (range.last - range.first) + range.first }
}

/**
 * Given a long range, create a new big BigDecimal.
 *
 * @param range the range to use when creating the long.
 * @param random used [Random] for generation.
 * @return random long within the given range.
 */
fun newBigDecimal(range: ClosedFloatingPointRange<Double>, random: Random = Random): BigDecimal {
    return newDouble(range, random).toBigDecimal()
}

/**
 * Given a long range, create a new big BigDecimal.
 *
 * @param range the range to use when creating the long.
 * @param random used [Random] for generation.
 * @return random long within the given range.
 */
fun newBigDecimal(range: LongRange, random: Random = Random): BigDecimal {
    return newDouble(range, random).toBigDecimal()
}

/**
 * Given a int range, create a new big BigDecimal.
 *
 * @param range the range to use when creating the long.
 * @param random used [Random] for generation.
 * @return random long within the given range.
 */
fun newBigDecimal(range: IntRange, random: Random = Random): BigDecimal {
    return BigDecimal.valueOf(newDouble(range, random))
}

/**
 * Create a new UUID.
 *
 * @param random used [Random] for generation.
 * @return random UUID.
 */
fun newUUID(random: Random = Random): UUID {
    return UUID(random.nextLong(), random.nextLong())
}
