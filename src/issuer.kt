/*
  Эмитент
 */

package core.issuer

import core.crypto.Crypto
import core.data.Banknote
import core.enums.ISO_4217_CODE
import java.security.PrivateKey
import java.security.PublicKey
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.collections.HashMap


class BankIssuer(
        private val bpk: PrivateKey,
        public val bok: PublicKey,
){
    private val walletSoks: MutableList<PublicKey> = mutableListOf()

    fun addWallet(sok: PublicKey): String{
        this.walletSoks.add(sok)
        val sokSignature = Crypto.signature(Crypto.hash(sok.toString()), this.bpk)
        return sokSignature
    }

    fun newBanknote(
            amount: Int,
            bin:Int,
            currencyCode: ISO_4217_CODE,
            bnid: String? =null,
    ): Banknote {

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
        val signature = Crypto.signature(hashValue, this.bpk)

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
}
