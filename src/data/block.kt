/*
   Декларирование одного блока
 */

package core.data

import core.crypto.Crypto
import java.security.PublicKey
import java.security.Signature
import java.util.*
import kotlin.test.expect


data class Block(
        /*
        Блок публичного блокчейна к каждой банкноте
        */
        val uuid: UUID,

        val parentUuid: UUID?,

        // BankNote id
        val bnid: String,

        // One Time Open key
        val otok: PublicKey,

        /// --->
        /// signature :
        val magic: String?,
        // val subscribeTransactionHash: ByteArray,
        // val subscribeTransactionSignature: String,
        val hashValue: ByteArray?,
        val signature: String?,

        )
{
    // TODO функция отображения в JSON для передачи на сервер


    public val _hashOtok: ByteArray get() {
        return Crypto.hash(this.otok.toString())
    }
}

data class ProtectedBlock(
        /*
        Сопроваждающий блок для дополнительного подтверждения на Сервере.
        */

        val parentSok: PublicKey?,
        val parentSokSignature: String?,
        val parentOtokSignature: String?,


        // Ссылка на Block
        val refUuid: UUID?,

        val sok: PublicKey?,
        val sokSignature: String?,
        val otokSignature: String?,


        )
{
        // TODO функция отображения в JSON для передачи на сервер

    public val _hashParentSok: ByteArray get() {
        return Crypto.hash(this.parentSok.toString())
    }
}

fun blockChain2Json(blockChain: List<Block>): String{
    // TODO написать core.data.blockChain2Json
    throw NotImplementedError("функция core.data.blockChain2Json ещё не написана!")
}