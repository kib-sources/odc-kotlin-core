/*

 */

package core.data

import core.enums.ISO_4217_CODE
import core.crypto.Crypto
import java.lang.Exception
import java.security.PublicKey

import core.utils.*

fun makeBanknoteHashValue(bin: Int, amount: Int, currencyCode: ISO_4217_CODE, bnid: String): ByteArray{
    return Crypto.hash(bin.toString(), amount.toString(), currencyCode.toString(), bnid)
}

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


    fun verification(bok: PublicKey): Boolean{
        val checkHashValue = makeBanknoteHashValue(bin, amount, currencyCode, bnid)
        if (!checkHashes( checkHashValue, hashValue)){
            throw Exception("HashValue не сходятся")
        }
        return Crypto.verifySignature(this.hashValue, this.signature, bok)
    }

}