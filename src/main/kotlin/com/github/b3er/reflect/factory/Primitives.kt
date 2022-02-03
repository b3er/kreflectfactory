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
import kotlin.random.Random


/**
 * @return a new boolean.
 */
fun newBoolean(): Boolean {
    return Random.nextBoolean()
}

/**
 * @return a new integer.
 */
fun newInt(): Int {
    return Random.nextInt()
}

/**
 * Given a min and a max, create a new integer.
 *
 * @param min the minimum value.
 * @param max the maximum value.
 * @return a value between min and max.
 */
fun newInt(min: Int? = null, max: Int? = null): Int {
    val minValue = min ?: Int.MIN_VALUE
    val maxValue = max ?: Int.MAX_VALUE

    require(minValue <= maxValue) {
        "The minimum value ($min) cannot higher than the maximum value ($max)."
    }

    val intRange = maxValue - minValue + 1
    return if (intRange < 0) {
        minValue + Random.nextInt(Int.MAX_VALUE)
    } else {
        minValue + Random.nextInt(intRange)
    }
}

/**
 * Given a range, create a new integer.
 *
 * @param range the range to use when creating the integer.
 * @return random integer within the given range.
 */
fun newInt(range: IntRange): Int {
    return range.random()
}

/**
 * Given a long range, create a new integer.
 *
 * @param range the range to use when creating the integer.
 * @return random integer within the given range.
 */
fun newInt(range: LongRange): Int {
    return range.let {
        it.first.coerceAtLeast(Int.MIN_VALUE.toLong()).toInt()..it.last.coerceAtMost(Int.MAX_VALUE.toLong()).toInt()
    }.random()
}

/**
 * Given a min and a max, create a new long.
 *
 * @param min the minimum value.
 * @param max the maximum value.
 * @return a value between min and max.
 */
fun newLong(min: Long? = null, max: Long? = null): Long {
    val minValue = min ?: Long.MIN_VALUE
    val maxValue = max ?: Long.MAX_VALUE

    if (minValue == Long.MIN_VALUE && maxValue == Long.MAX_VALUE) {
        return Random.nextLong()
    }

    require(minValue <= maxValue) {
        "The minimum value ($min) cannot higher than the maximum value ($max)."
    }

    val longRange = maxValue - minValue + 1
    return if (longRange < 0) {
        minValue + (Random.nextFloat() * Long.MAX_VALUE).toLong()
    } else {
        minValue + (Random.nextFloat() * longRange).toLong()
    }
}

/**
 * Given a long range, create a new integer.
 *
 * @param range the range to use when creating the long.
 * @return random long within the given range.
 */
fun newLong(range: LongRange): Long {
    return range.random()
}

/**
 * Creates a non-empty string that has a number of characters between the provided
 * range (defaults to 1-255).
 *
 * @param range the range representing the possible number of characters.
 * @return the string
 */
fun newString(range: IntRange = 1..255): String {
    val characters = ('a'..'z') + ('A'..'Z') + ('0'..'9')
    return (1..newInt(range)).map { characters.random() }.joinToString("")
}

/**
 * Given a long range, create a new double.
 *
 * @param range the range to use when creating the long.
 * @return random long within the given range.
 */
fun newDouble(range: LongRange): Double {
    return Random.nextDouble(range.first.toDouble(), range.last.toDouble())
}

/**
 * Given a range, create a new double.
 *
 * @param range the range to use when creating the long.
 * @return random long within the given range.
 */
fun newDouble(range: IntRange): Double {
    return Random.nextDouble(range.first.toDouble(), range.last.toDouble())
}

/**
 * Given a long range, create a new float.
 *
 * @param range the range to use when creating the long.
 * @return random long within the given range.
 */
fun newFloat(range: LongRange): Float {
    return newFloat(range.let {
        it.first.coerceAtLeast(Int.MIN_VALUE.toLong()).toInt()..it.last.coerceAtMost(Int.MAX_VALUE.toLong()).toInt()
    })
}

/**
 * Given a range, create a new float.
 *
 * @param range the range to use when creating the long.
 * @return random long within the given range.
 */
fun newFloat(range: IntRange): Float {
    return Random.nextFloat().let { it * (range.last - range.first) + range.first }
}

/**
 * Given a long range, create a new big BigDecimal.
 *
 * @param range the range to use when creating the long.
 * @return random long within the given range.
 */
fun newBigDecimal(range: LongRange): BigDecimal {
    return newDouble(range).toBigDecimal()
}

/**
 * Given a int range, create a new big BigDecimal.
 *
 * @param range the range to use when creating the long.
 * @return random long within the given range.
 */
fun newBigDecimal(range: IntRange): BigDecimal {
    return BigDecimal.valueOf(newDouble(range))
}
