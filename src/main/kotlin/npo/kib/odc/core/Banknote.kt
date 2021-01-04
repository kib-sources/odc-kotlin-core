package npo.kib.odc.core

import npo.kib.odc.crypto.Crypto
import java.security.PrivateKey
import java.security.PublicKey
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

const val RUSSIAN_RUBLE = 643

data class Banknote(
    val bin: Int,

    val amount: Int,

    val currencyCode: Int,

    // BankNote id
    val bnid: String,

    // hash
    val hash: ByteArray,

    // signature
    val signature: String,
) {
    fun verify(bok: PublicKey): Boolean {
        val hash = makeHash(bnid)

        if (!hash.contentEquals(this.hash))
            return false

        if (!Crypto.verifySignature(this.hash, this.signature, bok))
            return false

        return true
    }

    companion object {
        fun makeBnid(bin: Int): String {
            val now = LocalDateTime.now()
            val formatter = DateTimeFormatter.ofPattern("YYYYMMddHHmmssSSSSSS")
            Thread.sleep(1) // необходимо поспать, чтобы гарантировать уникальный номер
            return "${bin}-${now.format(formatter)}"
        }

        fun makeHash(bnid: String): ByteArray = Crypto.hash(bnid)

        fun init(bpk: PrivateKey, bin: Int, amount: Int, currencyCode: Int = RUSSIAN_RUBLE): Banknote {
            val bnid = makeBnid(bin)
            val hash = makeHash(bnid)
            val signature = Crypto.signature(hash, bpk)

            return Banknote(
                bin,
                amount = amount,
                currencyCode = currencyCode,
                bnid = bnid,
                hash = hash,
                signature = signature
            )
        }
    }
}
