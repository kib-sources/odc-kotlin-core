/*
    Вспомогательные функции
 */

package core.utils

import kotlin.random.Random

fun randomMagic(): String = (1..15).asSequence()
        .map { Random.nextInt(0, 10) }
        .map { it.toString() }
        .reduce { acc, it -> acc + it }
