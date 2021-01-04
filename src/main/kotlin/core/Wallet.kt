package core

import crypto.Crypto
import java.security.PrivateKey
import java.security.PublicKey
import java.util.*
import kotlin.collections.HashMap
import kotlin.random.Random.Default.nextInt

class Wallet(val spk: PrivateKey, val sok: String, val sok_signature: String) {

    val _bag = HashMap<UUID, PrivateKey>()

    fun new_block_params(bnid: String, parent_uuid: UUID? = null): TransactionBlock {
        val (publicKey, privateKey) = Crypto.initPair()

        val uuid = UUID.randomUUID()

        val transactionHash = transaction_hash(uuid, parent_uuid, publicKey, bnid)
        val initWalletSignature = Crypto.signature(transactionHash, spk)

        _bag[uuid] = privateKey

        return TransactionBlock(uuid, parent_uuid, publicKey, transactionHash, initWalletSignature)
    }

    fun subscribe(uuid: UUID, parent_uuid: UUID, bnid: String): Subscription {
        if (parent_uuid in _bag.keys)
            throw Exception("Уже передан блок с uuid=${parent_uuid}")

        val otpk = _bag[parent_uuid]
            ?: throw Exception("Блока с uuid=${parent_uuid} никогда не было в кошельке")

        val magic = random_magic()

        val transactionHash = subscribe_transaction_hash(uuid, magic, bnid)
        val transactionSignature = Crypto.signature(transactionHash, otpk)

        // Удаляем ключ, чтобы более ни разу нельзя было подписывать
        //   в нормальном решении необходимо хранение на доверенном носителе, например на SIM
        _bag.remove(parent_uuid)

        return Subscription(magic, transactionHash, transactionSignature)
    }
}

data class Subscription(
    val magic: String,
    val _subscribe_transaction_hash: ByteArray,
    val _subscribe_transaction_signature: String
)

data class TransactionBlock(
    val uuid: UUID,
    val parent_uuid: UUID?,
    val otok: PublicKey,
    val _transaction_hash: ByteArray,
    val _init_wallet_signature: String
)

fun transaction_hash(uuid: UUID, parent_uuid: UUID?, otok: PublicKey, bnid: String): ByteArray =
    Crypto.hash(uuid.toString(), parent_uuid.toString(), otok.toString(), bnid)

fun subscribe_transaction_hash(uuid: UUID, magic: String, bnid: String): ByteArray =
    Crypto.hash(uuid.toString(), magic, bnid)

fun random_magic(): String = (1..15).asSequence()
    .map { nextInt(0, 10) }
    .map { it.toString() }
    .reduce { acc, it -> acc + it }

fun bank_subscribe(uuid: UUID, bpk: PrivateKey, bnid: String): Subscription {
    val magic = random_magic()

    val transactionHash = subscribe_transaction_hash(uuid, magic, bnid)
    val transactionSignature = Crypto.signature(transactionHash, bpk)

    return Subscription(magic, transactionHash, transactionSignature)
}
