/*
    Вспомогательные функции
 */

package core.utils

import kotlin.random.Random

fun randomMagic(): String = (1..15).asSequence()
        .map { Random.nextInt(0, 10) }
        .map { it.toString() }
        .reduce { acc, it -> acc + it }


fun checkHashes(hash1: ByteArray, hash2: ByteArray): Boolean{

    if (hash1.count() != hash2.count()){
        return false
    }

    for (i in 0 until hash1.count()){
        if (hash1[i] != hash2[i]){
            return false
        }
    }
    return true
}