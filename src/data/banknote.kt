/*

 */

package core.data

import java.security.PrivateKey
import java.time.LocalDate
import java.time.format.DateTimeFormatter

import core.enums.ISO_4217_CODE
import core.crypto.Crypto

data class Banknote(
        val bin: Int,

        val amount: Int,

        val currencyCode: ISO_4217_CODE,

        // BankNote id
        val bnid: String,

        // hash
        // val hash: ByteArray,
        val hashValue: ByteArray,
        val signature: String,
) {

    // TODO сохранить в JSON
    // TODO выгрузить из JSON-а
    // TODO сохранить в protobuf
    // TODO выгрузить из protobuf

}