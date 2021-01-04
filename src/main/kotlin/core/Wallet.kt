package core

import crypto.Crypto
import java.security.PrivateKey
import java.security.PublicKey
import java.util.*
import kotlin.collections.HashMap
import kotlin.random.Random.Default.nextInt

class Wallet(val spk: PrivateKey, val sok: PublicKey, val sokSignature: String) {

    private val bag = HashMap<UUID, PrivateKey>()

    fun newBlockParams(bnid: String, parentUuid: UUID? = null): TransactionBlock {
        val (publicKey, privateKey) = Crypto.initPair()

        val uuid = UUID.randomUUID()

        val transactionHash = getTransactionHash(uuid, parentUuid, publicKey, bnid)
        val initWalletSignature = Crypto.signature(transactionHash, spk)

        bag[uuid] = privateKey

        return TransactionBlock(uuid, parentUuid, publicKey, transactionHash, initWalletSignature)
    }

    fun subscribe(uuid: UUID, parentUuid: UUID, bnid: String): Subscription {
        val otpk = bag[parentUuid]
            ?: throw Exception("Уже передан блок с uuid=$parentUuid или данного блока никогда не было в кошельке")

        val magic = randomMagic()

        val transactionHash = getSubscribeTransactionHash(uuid, magic, bnid)
        val transactionSignature = Crypto.signature(transactionHash, otpk)

        // Удаляем ключ, чтобы более ни разу нельзя было подписывать
        //   в нормальном решении необходимо хранение на доверенном носителе, например на SIM
        bag.remove(parentUuid)

        return Subscription(magic, transactionHash, transactionSignature)
    }
}

data class Subscription(
    val magic: String,
    val subscribeTransactionHash: ByteArray,
    val subscribeTransactionSignature: String
)

data class TransactionBlock(
    val uuid: UUID,
    val parentUuid: UUID?,
    val otok: PublicKey,
    val transactionHash: ByteArray,
    val initWalletSignature: String
)

fun getTransactionHash(uuid: UUID, parentUuid: UUID?, otok: PublicKey, bnid: String): ByteArray =
    Crypto.hash(uuid.toString(), parentUuid.toString(), otok.toString(), bnid)

fun getSubscribeTransactionHash(uuid: UUID, magic: String, bnid: String): ByteArray =
    Crypto.hash(uuid.toString(), magic, bnid)

fun randomMagic(): String = (1..15).asSequence()
    .map { nextInt(0, 10) }
    .map { it.toString() }
    .reduce { acc, it -> acc + it }

fun createBankSubscription(uuid: UUID, bpk: PrivateKey, bnid: String): Subscription {
    val magic = randomMagic()

    val transactionHash = getSubscribeTransactionHash(uuid, magic, bnid)
    val transactionSignature = Crypto.signature(transactionHash, bpk)

    return Subscription(magic, transactionHash, transactionSignature)
}
