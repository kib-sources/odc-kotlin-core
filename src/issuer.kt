/*
  Эмитент
 */

package core.issuer

import java.security.PrivateKey
import java.security.PublicKey
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.collections.HashMap


import core.crypto.Crypto
import core.data.*
import core.enums.ISO_4217_CODE
import java.lang.Exception
import kotlin.random.Random

import core.utils.*

class BankIssuer(
        private val bpk: PrivateKey,
        public val bok: PublicKey,
){
    private val walletSoks: MutableList<PublicKey> = mutableListOf()

    fun pushBlock(banknote: Banknote, block: Block, protectedBlock: ProtectedBlock): Exception?{
        // Напишите функцию сохранения блока.
        //   Для этого сделайте наследника от BankIssuer
        return null
    }

    fun signature(banknote: Banknote, firstBlock: Block, protectedBlock: ProtectedBlock): Block {

        assert(firstBlock.parentUuid == null)
        assert(banknote.bnid == firstBlock.bnid)
        assert(firstBlock.uuid == protectedBlock.refUuid)

        if ( ! this.walletSoks.contains(protectedBlock.sok)) {
            throw Exception("Кошелёк не обнаружен в банке-эмитенте.")
        }
        if (protectedBlock.otokSignature == null){
            throw Exception("protectedBlock.otokSignature is null")
        }
        if (protectedBlock.sok == null) {
            throw Exception("protectedBlock.sok is null")
        }

        if (Crypto.verifySignature(Crypto.hash(firstBlock.otok.toString()), protectedBlock.otokSignature, protectedBlock.sok) == false){
            throw Exception("otok сгенерирован не кошельком!")
        }

        val magic = randomMagic()
        val hashValue = makeBlockHashValue(firstBlock.uuid, null, firstBlock.bnid, magic)
        val signature = Crypto.signature(hashValue, this.bpk)

        val ret_block = Block(
                uuid = firstBlock.uuid,
                parentUuid = null,
                bnid = firstBlock.bnid,
                otok = firstBlock.otok,
                magic = magic,
                hashValue=hashValue,
                signature=signature,
        )

        return ret_block
    }

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
