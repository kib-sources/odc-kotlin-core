/*
   Декларирование одного блока
 */

package core.data

import java.security.PublicKey
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
}

data class ProtectedBlock(
        /*
        Сопроваждающий блок для дополнительного подтверждения на Сервере.
        */

        // Ссылка на Block
        val refUuid: UUID,

        val sok: PublicKey,
        val sokSignature: String,
        val parentSok: PublicKey,
        val parentSokSignature: String,

        )
{
        // TODO функция отображения в JSON для передачи на сервер
}

fun blockChain2Json(blockChain: List<Block>): String{
    // TODO написать core.data.blockChain2Json
    throw NotImplementedError("функция core.data.blockChain2Json ещё не написана!")
}