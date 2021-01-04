package examples

import core.*
import crypto.Crypto
import crypto.getString
import org.junit.Test
import java.security.PublicKey

class SecondUseCase {
    private val walletAlice = Wallet(spk = A_SPK, sok = A_SOK, sokSignature = sok_signature(A_SOK))
    val walletBob = Wallet(spk = B_SPK, sok = B_SOK, sokSignature = sok_signature(B_SOK))

    private val banknote500 = Banknote.init(bpk = BPK, bin = BIN, amount = 500)
    val banknote100 = Banknote.init(bpk = BPK, bin = BIN, amount = 500)

    @Test
    fun useCase() {
        val blockchain = mutableListOf<OneBlock>()

        val block1 = bankToAlice()
        blockchain.add(block1)
    }

    private fun bankToAlice(): OneBlock {
        // ------------------------------------------------------------------------------------------------------------
        // Передача купюры: банк -> Алиса
        // ------------------------------------------------------------------------------------------------------------
        val bnid1 = banknote500.bnid

        // ------- Канал связи --------------------
        // Банк передаёт bnid Алисе по каналу связи

        // -------- Сторона: Алиса ----------------
        // Алиса создаёт новый блок
        val (uuid, _, otok, _, initWalletSignature) = walletAlice.newBlockParams(bnid = bnid1)

        // ------- Канал связи --------------------
        // Передача данных от Алисы Банку: uuid, otok, initWalletSignature, sok, sokSignature
        val sok = walletAlice.sok
        val sokSignature = walletAlice.sokSignature

        // ------- Сторона: Банк ------------------
        // Верификация
        assert {
            val h = Crypto.hash(sok.getString())
            Crypto.verifySignature(h, sokSignature, BOK)
        }

        assert {
            val h = getTransactionHash(uuid, null, otok, bnid1)
            Crypto.verifySignature(h, initWalletSignature, sok)
        }

        // Банк подписывает
        val (magic, subscribeTransactionHash, subscribeTransactionSignature) = createBankSubscription(
            bnid = bnid1,
            uuid = uuid,
            bpk = BPK
        )

        // ------- Канал связи -----------------
        // Передача данных от банка Алисе: magic, subscribeTransactionHash, subscribeTransactionSignature

        // -------- Сторона: Алиса -------------
        return OneBlock(
            uuid = uuid,
            parentUuid = null,
            bnid = bnid1,
            otok = otok,
            magic = magic,
            subscribeTransactionHash = subscribeTransactionHash,
            subscribeTransactionSignature = subscribeTransactionSignature,
        )
    }

    private fun sok_signature(sok: PublicKey): String {
        // В нормальном решении SOK должен изначально быть подписанным внутри SIM карты
        val h = Crypto.hash(sok.getString())
        val sokSignature = Crypto.signature(h, BPK)
        return sokSignature
    }

    private fun assert(block: () -> Boolean) {
        assert(block())
    }

    fun assertThrow(msg: String, block: () -> Any) {
        val res = runCatching { block() }

        if (res.isSuccess)
            throw AssertionError(msg)
    }
}