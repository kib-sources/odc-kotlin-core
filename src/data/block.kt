/*
   Декларирование одного блока
 */

package core.data

import core.crypto.Crypto
import core.utils.checkHashes
import java.lang.Exception
import java.security.PublicKey
import java.util.*


fun makeBlockHashValue(uuid: UUID, parentUuid: UUID?, bnid: String, magic: String): ByteArray{
    return if (parentUuid == null){
        Crypto.hash(uuid.toString(), bnid, magic)
    }else{
        Crypto.hash(uuid.toString(), parentUuid.toString(), bnid, magic)
    }
}

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

    fun verification(publicKey: PublicKey): Boolean{
        // publicKey -- otok or bok
        if (magic == null){
            throw Exception("Блок не до конца определён. Не задан magic")
        }
        if (hashValue == null){
            throw Exception("Блок не до конца определён. Не задан hashValue")
        }
        if (signature == null){
            throw Exception("Блок не до конца определён. Не задан signature")
        }
        val hashValueCheck = makeBlockHashValue(uuid, parentUuid, bnid, magic)
        if (!checkHashes(hashValueCheck, hashValue)){
            throw Exception("Некорректно подсчитан hashValue")
        }
        return Crypto.verifySignature(hashValue, signature, publicKey)
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