package core

import crypto.Crypto
import java.security.PrivateKey
import java.security.PublicKey
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

const val RUSSIAN_RUBLE = 643

data class Banknote(
    val bin: Int,

    val amount: Int,

    val currency_code: Int,

    // BankNote id
    val bnid: String,

    // hash
    val hash: ByteArray,

    // signature
    val signature: String,
) {
    fun verify(bok: PublicKey): Boolean {
        val hash = make_hash(bnid)

        if (!hash.contentEquals(this.hash))
            return false

        if (!Crypto.verifySignature(this.hash, this.signature, bok))
            return false

        return true
    }

    companion object {
        fun make_bnid(bin: Int): String {
            val now = LocalDateTime.now()
            val formatter = DateTimeFormatter.ofPattern("YYYYMMddHHmmssSSSSSS")
            Thread.sleep(1) // необходимо поспать, чтобы гарантировать уникальный номер
            return "${bin}-${now.format(formatter)}"
        }

        fun make_hash(bnid: String): ByteArray = Crypto.hash(bnid)

        fun init(bpk: PrivateKey, bin: Int, amount: Int, currency_code: Int = RUSSIAN_RUBLE): Banknote {
            val bnid = make_bnid(bin)
            val hash = make_hash(bnid)
            val signature = Crypto.signature(hash, bpk)

            return Banknote(
                bin,
                amount = amount,
                currency_code = currency_code,
                bnid = bnid,
                hash = hash,
                signature = signature
            )
        }
    }
}
