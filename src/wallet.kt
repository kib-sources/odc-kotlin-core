/*
    Модуль, реализующий кошелёк.
    В МЖП (Минимально жизнеспособном продукте) должен быть в защищёной части телефона (SIM карта)

    SIC!
    В рамках презентации -- внутри самого приложения, что не безопасно .
 */
package core.wallet

import java.security.PrivateKey
import java.security.PublicKey
import java.util.*
import kotlin.collections.HashMap
import kotlin.random.Random.Default.nextInt

import java.util.*

import core.crypto.Crypto

import core.data.*
import core.utils.*


class Wallet(
        private val spk: PrivateKey,
        public val sok: PublicKey,
        public val sokSignature: String,

        // Сделать глобальной переменной
        public val BOK: PublicKey,

        )
{
    private val bag = HashMap<UUID, PrivateKey>()


    var BOK_str = """-----BEGIN RSA PUBLIC KEY-----
MEgCQQCZScdB8AFwcrZDOLVsBT7m+KyuARWixZCstV99oOMYD318o0rhAqSYk/3Q
nxV31GMYcJv7qABEqnowEkTGDh1TAgMBAAE=
-----END RSA PUBLIC KEY-----""";

    // TODO определить BOK
    // var BOK = X509EncodedKeySpec()
    // var BOK = null


    // TODO зарузка в файл
    // TODO выгрузка из файла

    fun firstBlock(banknote: Banknote): Pair<Block, ProtectedBlock>{
        val uuid = UUID.randomUUID()

        val (otok, otpk) = Crypto.initPair()

        this.bag[uuid] = otpk

        val block = Block(
                uuid=uuid,
                parentUuid=null,
                bnid=banknote.bnid,
                otok=otok,
                magic=null,
                hashValue=null,
                signature=null,
        )
        val otokSignature = Crypto.signature(Crypto.hash(otok.toString()), this.spk)

        val protectedBlock = ProtectedBlock(
                parentSok=null,
                parentSokSignature=null,
                parentOtokSignature=null,
                refUuid=uuid,
                sok=this.sok,
                sokSignature=this.sokSignature,
                otokSignature=otokSignature,
        )

        return Pair(block, protectedBlock)
    }

    fun init(parentBlock: Block): ProtectedBlock{

        // TODO Необходимо взять parentBlock.otok и подписать его
        val parentOtokSignature = ""

        val protectedBlock = ProtectedBlock(
                parentSok=this.sok,
                parentSokSignature=this.sokSignature,
                parentOtokSignature=parentOtokSignature,
                refUuid=null,
                sok=null,
                sokSignature=null,
                otokSignature=null,
        )
        return protectedBlock
    }
    fun serverInitVerification(banknote: Banknote): java.lang.Exception?{
        // TODO данный вызов может происходить только от сервера.
        //  Теоретически нужно проверить, что банкноты корректно подписаны
        // throw NotImplementedError("serverInitVerification")
        return null
    }

    fun initVerification(parentBlock: Block, protectedBlock: ProtectedBlock): Exception?{

        if (parentBlock.parentUuid == null) {
            // Передача от банка к владельцу
            throw Exception("initVerification не требуется для передачи купюры первому владельцу. См. serverInitVerification")
        }

        // Передача от владельца владельцу

        if (protectedBlock.parentOtokSignature == null)  {
            throw java.lang.Exception("parentOtokSignature не указан!")
        }
        if (protectedBlock.parentSokSignature == null)  {
            throw java.lang.Exception("parentSokSignature не указан!")
        }
        if (protectedBlock.sok == null)  {
            throw java.lang.Exception("parentSokSignature не указан!")
        }

        if (Crypto.verifySignature(protectedBlock._hashParentSok, protectedBlock.parentSokSignature, this.BOK) == false){
           throw java.lang.Exception("SOK отправителья не подписан")
        }
        if (Crypto.verifySignature(parentBlock._hashOtok, protectedBlock.parentOtokSignature, protectedBlock.sok) == false){
            throw java.lang.Exception("OTOK отправителья не подписан")
        }
        return null
    }

    fun acceptanceInit(parentBlock: Block, protectedBlock: ProtectedBlock): Pair<Block, ProtectedBlock>{
        throw NotImplementedError("Не реализована фуцнкия Wallet->acceptance_init")
        // val childBlock = Block(...)
        // TODO необходимо добавть в protectedBlock поля:
        //   1. refUuid
        //   2. sok
        //   3. sokSignature
        //   4. otokSignature
        // return Paar(childBlock, protectedBlock)
    }

    fun acceptanceInitVerification(parentBlock: Block, childBlock: Block, protectedBlock: ProtectedBlock): Exception?{
        throw NotImplementedError("Не реализована фуцнкия Wallet->acceptance_init_verification")
        return null // Если нет ошибки
        return Exception("Текст ошибки") // Если есть ошибка
    }

    fun signature(childBlock: Block, protectedBlock: ProtectedBlock): Pair<Block, ProtectedBlock>{

        // TODO добавить в childBlock:
        //   1. magic
        //   2. hashValue
        //   3. signature

        return Pair(childBlock, protectedBlock)
    }
}


// Пример из POC-а
// Не используется. Написан Александром, не смотрел что там.
class WalletFromPythonPOC(
        private val spk: PrivateKey,
        val sok: PublicKey,
        val sokSignature: String,
        )
{

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


fun createBankSubscription(uuid: UUID, bpk: PrivateKey, bnid: String): Subscription {
    val magic = randomMagic()

    val transactionHash = getSubscribeTransactionHash(uuid, magic, bnid)
    val transactionSignature = Crypto.signature(transactionHash, bpk)

    return Subscription(magic, transactionHash, transactionSignature)
}