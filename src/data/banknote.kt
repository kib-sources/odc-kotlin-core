/*

 */

package core.data

import core.crypto.Crypto
import core.enums.ISO_4217_CODE
import core.utils.checkHashes
import java.security.PublicKey

fun makeBanknoteHashValue(bin: Int, amount: Int, currencyCode: ISO_4217_CODE, bnid: String): ByteArray{
    return Crypto.hash(bin.toString(), amount.toString(), currencyCode.toString(), bnid)
}

// @Serializable(with = kotlinx.serialization.json.JsonElementSerializer::class)
// @Serializable
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