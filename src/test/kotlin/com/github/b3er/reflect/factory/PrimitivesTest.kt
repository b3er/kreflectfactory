@file:Suppress("ClassName")

package com.github.b3er.reflect.factory

import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.RepeatedTest
import org.junit.jupiter.api.assertThrows
import kotlin.math.abs
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class PrimitivesTest {
    @Nested
    inner class `#newBoolean` {
        @Nested
        inner class `when called` {
            @RepeatedTest(10)
            fun `returns new boolean`() {
                val value = newBoolean()
                assertNotNull(value)
                assertIs<Boolean>(value)
            }
        }

        @Nested
        inner class `when called multiple times` {
            @RepeatedTest(10)
            fun `returns different values`() {
                val values = (100..200).asSequence().map { newBoolean() }
                assertEquals(2, values.distinct().count())
            }
        }
    }

    @Nested
    inner class `#newInt` {
        @Nested
        inner class `when called` {
            @RepeatedTest(10)
            fun `returns new integer`() {
                val value = newInt()
                assertNotNull(value)
                assertIs<Int>(value)
            }
        }

        @Nested
        inner class `when called multiple times` {
            @RepeatedTest(10)
            fun `returns different values`() {
                val values = (100..200).asSequence().map { newInt() }
                // Collision is possible, so guaranteeing there are at least 1/2 of the
                // number of minimum values should suffice. The error case here is if there's
                // only one distinct value.
                assert(values.distinct().count() > 50)
            }
        }
    }

    @Nested
    inner class `#newInt(int, int)` {
        @Nested
        inner class `when called` {
            @Nested
            inner class `with min set` {
                @RepeatedTest(10)
                fun `returns only values starting after min and before max int`() {
                    val min = newInt()
                    assertTrue(newInt(min = min) in min..Int.MAX_VALUE)
                }
            }

            @Nested
            inner class `with max set` {
                @RepeatedTest(10)
                fun `returns only values starting after min and before max int`() {
                    val max = newInt()
                    assertTrue(newInt(max = max) in Int.MIN_VALUE..max)
                }
            }

            @Nested
            inner class `with min and max` {
                @RepeatedTest(10)
                fun `returns only values starting after min and before max int`() {
                    val int = newInt()
                    val otherInt = newInt()
                    if (int < otherInt) {
                        assertTrue(newInt(int, otherInt) in int..otherInt)
                    } else {
                        assertTrue(newInt(otherInt, int) in otherInt..int)
                    }
                }
            }

            @Nested
            inner class `with min and max inverted` {
                @RepeatedTest(10)
                fun `throws exception`() {
                    val int = newInt()
                    val otherInt = newInt()
                    if (int < otherInt) {
                        assertThrows<IllegalArgumentException> { newInt(otherInt, int) }
                    } else {
                        assertThrows<IllegalArgumentException> { newInt(int, otherInt) }
                    }
                }
            }
        }
    }

    @Nested
    inner class `#newInt(range)` {
        @Nested
        inner class `when called` {
            @Nested
            inner class `with min and max set` {
                @RepeatedTest(10)
                fun `returns only values between min and max`() {
                    // Kotlin Ranges can only be positive.
                    val int = abs(newInt())
                    val otherInt = abs(newInt())
                    if (int < otherInt) {
                        assertTrue(newInt(int..otherInt) in int..otherInt)
                    } else {
                        assertTrue(newInt(otherInt..int) in otherInt..int)
                    }
                }
            }
        }
    }

    @Nested
    inner class `#newLong(long, long)` {
        @Nested
        inner class `when called` {
            @Nested
            inner class `with min set` {
                @RepeatedTest(10)
                fun `returns only values starting after min and before max long`() {
                    val min = newLong()
                    assertTrue(newLong(min = min) in min..Long.MAX_VALUE)
                }
            }

            @Nested
            inner class `with max set` {
                @RepeatedTest(10)
                fun `returns only values starting after min and before max long`() {
                    val max = newLong()
                    assertTrue(newLong(max = max) in Long.MIN_VALUE..max)
                }
            }

            @Nested
            inner class `with min and max` {
                @RepeatedTest(10)
                fun `returns only values starting after min and before max long`() {
                    val long = newLong()
                    val otherLong = newLong()
                    if (long < otherLong) {
                        assertTrue(newLong(long, otherLong) in long..otherLong)
                    } else {
                        assertTrue(newLong(otherLong, long) in otherLong..long)
                    }
                }
            }

            @Nested
            inner class `with min and max inverted` {
                @RepeatedTest(10)
                fun `throws exception`() {
                    val long = newLong()
                    val otherLong = newLong()
                    if (long < otherLong) {
                        assertThrows<IllegalArgumentException> { newLong(otherLong, long) }
                    } else {
                        assertThrows<IllegalArgumentException> { newLong(long, otherLong) }
                    }
                }
            }
        }
    }

    @Nested
    inner class `#newString` {
        @Nested
        inner class `with no ranges` {
            @RepeatedTest(10)
            fun `returns a string with exactly or less than 255 characters`() {
                assertTrue(newString().length in 1..255)
            }
        }

        @Nested
        inner class `with range` {
            @RepeatedTest(10)
            fun `returns a string within the range`() {
                val lowerBound = newInt(1..100)
                val higherBound = lowerBound + newInt(1..100)
                assertTrue(newString(lowerBound..higherBound).length in lowerBound..higherBound)
            }
        }
    }
}
