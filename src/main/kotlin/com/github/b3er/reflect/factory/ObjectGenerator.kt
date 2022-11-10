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

import com.github.b3er.reflect.factory.generators.RandomObjectGenerator
import kotlin.reflect.KClass
import kotlin.reflect.KParameter
import kotlin.reflect.KType
import kotlin.reflect.KTypeParameter
import kotlin.reflect.full.isSubtypeOf
import kotlin.reflect.full.withNullability
import kotlin.reflect.typeOf

interface ObjectGenerator {
    fun generate(type: KType): Any = with(type) {
        val classifier = classifier
        when {
            isTypeOf<BooleanArray>() -> generateSequence(typeOf<Boolean>()).toList().toTypedArray()
            isTypeOf<CharArray>() -> generateSequence(typeOf<Char>()).toList().toTypedArray()
            isTypeOf<ShortArray>() -> generateSequence(typeOf<Short>()).toList().toTypedArray()
            isTypeOf<IntArray>() -> generateSequence(typeOf<Int>()).toList().toTypedArray()
            isTypeOf<LongArray>() -> generateSequence(typeOf<Long>()).toList().toTypedArray()
            isTypeOf<FloatArray>() -> generateSequence(typeOf<Float>()).toList().toTypedArray()
            isTypeOf<DoubleArray>() -> generateSequence(typeOf<Double>()).toList().toTypedArray()
            isSubTypeOf<Collection<*>>() -> generateList<Any, List<Any>>(this)
            isSubTypeOf<Array<*>>() -> generateList<Any, List<Any>>(this).toTypedArray()
            isSubTypeOf<Map<*, *>>() -> generateMap<Any, Any, Map<Any, Any>>(this)
            classifier is KClass<*> -> generateClass(type, classifier)
            else -> NotImplementedError("Type: $this not implemented")
        }
    }

    fun generate(parameter: KParameter): Any = generate(parameter.type)

    fun generateSequence(type: KType): Sequence<*> = sequenceOf<Any>(generate(type))

    fun generateClass(type: KType, klass: KClass<*>): Any {
        val primaryConstructor = klass.constructors.first()
        val args = primaryConstructor.parameters.associateBy({ it }) { parameter ->
            if (parameter.type.classifier is KTypeParameter) {
                val name = (parameter.type.classifier as KTypeParameter).name
                val resolved = type.arguments[klass.typeParameters.indexOfFirst { it.name == name }].type
                    ?: throw IllegalArgumentException("Can't resolve parameter $parameter for $type")
                generate(resolved)
            } else {
                generate(parameter)
            }

        }
        return primaryConstructor.callBy(args)
    }

    @Suppress("UNCHECKED_CAST")
    private fun <T, R : List<T>> generateList(type: KType): R {
        val arguments = type.arguments
        val listType = arguments.firstOrNull()?.type
            ?: throw IllegalArgumentException("Can't resolve type ${arguments.firstOrNull()} for list $this")

        val list = generateSequence(listType).toList()
        val classifier = type.classifier
        return when (classifier) {
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


    @Suppress("UNCHECKED_CAST")
    private fun <K, V, R : Map<K, V>> generateMap(type: KType): R {
        val arguments = type.arguments
        val keyType = arguments.getOrNull(0)?.type
            ?: throw IllegalArgumentException("Can't resolve key type ${arguments.firstOrNull()} for map $this")
        val valueType = arguments.getOrNull(1)?.type
            ?: throw IllegalArgumentException("Can't resolve value type ${arguments.firstOrNull()} for map $this")

        val map = generateObjectsMap(
            generateSequence(keyType).toList(),
            generateSequence(valueType).toList()
        )

        val classifier = type.classifier
        return when (classifier) {
            Map::class, MutableMap::class, AbstractMap::class, HashMap::class, LinkedHashMap::class -> map
            else -> {
                val mapClass = (classifier as KClass<*>)
                mapClass.constructors.find {
                    it.parameters.size == 1 && it.parameters.first().type.isSubtypeOf(typeOf<Map<K, V>>())
                }?.call(map) ?: throw IllegalStateException("Can't find constructor with map argument for type $this")
            }
        } as R
    }

    private fun <K, V> generateObjectsMap(keys: List<K>, values: List<V>): Map<K, V> {
        val result = LinkedHashMap<K, V>(keys.size)
        repeat(keys.size) {
            result[keys[it]] = values[it]
        }
        return result
    }

    companion object {
        inline fun <reified T : Any> KType.isTypeOf(ignoreNullability: Boolean = true): Boolean =
            (if (ignoreNullability) withNullability(false) else this) == typeOf<T>()

        inline fun <reified T : Any> KType.isSubTypeOf(ignoreNullability: Boolean = true): Boolean =
            (if (ignoreNullability) withNullability(false) else this).isSubtypeOf(typeOf<T>())

        @Suppress("UNCHECKED_CAST")
        fun KType.enumConstants(): Array<Any> = (classifier as KClass<*>).java.enumConstants as Array<Any>
    }
}

inline fun <reified T> newObject(
    generator: ObjectGenerator = RandomObjectGenerator.Default,
    mapper: T.() -> T = { this }
): T = mapper(generator.generate(typeOf<T>()) as T)

inline fun <reified T> newObjects(
    size: Int,
    generator: ObjectGenerator = RandomObjectGenerator.Default,
    noinline mapper: T.() -> T = { this }
): List<T> = generateSequence { newObject(generator, mapper) }.take(size).toList()
