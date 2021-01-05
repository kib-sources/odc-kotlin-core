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


fun newBanknote(
        bpk: PrivateKey,
        amount: Int,
        bin:Int,
        currencyCode: ISO_4217_CODE,
        bnid: String? =null,
): Banknote{

    val bnid_: String = when (bnid) {
        null -> {
            val d = LocalDate.now()
            val dateTimeFormatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS")
            d.format(dateTimeFormatter)
        }
        else -> {
            bnid
        }
    }
    val hashValue = Crypto.hash(bin.toString(), amount.toString(), currencyCode.toString(), bnid_)
    val signature = Crypto.signature(hashValue, bpk)

    val banknote = Banknote(
            bin=bin,
            amount=amount,
            currencyCode=currencyCode,
            bnid=bnid_,
            hashValue=hashValue,
            signature=signature,
    )
    return banknote
}