package examples

import core.*
import crypto.Crypto
import crypto.getString
import org.junit.Test
import org.junit.Assert.*
import java.security.PublicKey

class SecondUseCase {
    private val banknote500 = Banknote.init(bpk = BPK, bin = BIN, amount = 500)

    private val walletAlice = Wallet(spk = A_SPK, sok = A_SOK, sokSignature = sok_signature(A_SOK))
    private val walletBob = Wallet(spk = B_SPK, sok = B_SOK, sokSignature = sok_signature(B_SOK))

    private lateinit var aliceInitSignature: String
    private val blockchain = mutableListOf<OneBlock>()

    @Test
    fun useCase() {
        val block1 = bankToAlice()
        blockchain.add(block1)

        val block2 = aliceToBob(block1)
        blockchain.add(block2)

        validateBlockchain()
        println("Корректны все блоки!")
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
        aliceInitSignature = initWalletSignature

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

    private fun aliceToBob(prevBlock: OneBlock): OneBlock {
        // -----------------------------------------------------------------------------------------------------------------
        // -----------------------------------------------------------------------------------------------------------------
        // Второй блок
        // Передача купюры: Алиса -> Боб
        // -----------------------------------------------------------------------------------------------------------------

        // ------------ Канал связи -------------------
        // Алиса передаёт Бобу
        val parentUuid1 = prevBlock.uuid
        val bnid = prevBlock.bnid

        // ----------- Сторона: Боб -------------------
        // Верификация Боб-а

        assert {
            val h = Crypto.hash(walletAlice.sok.getString())
            Crypto.verifySignature(h, walletAlice.sokSignature, BOK)
        }

        assert {
            val h = getTransactionHash(parentUuid1, null, prevBlock.otok, bnid)
            Crypto.verifySignature(h, aliceInitSignature, walletAlice.sok)
        }

        // Боб создаёт новый блок
        val (uuid, parent_uuid, otok, _, init_wallet_signature) = walletBob.newBlockParams(
            bnid = bnid,
            parentUuid = parentUuid1
        )

        // ----------- Канал связи --------------------
        // Передача данных от Боба Алисе через канал связи: uuid, parent_uuid, otok, sok, sok_signature_, init_wallet_signature
        val sokBob = walletBob.sok
        val sokSignatureBob = walletBob.sokSignature

        // ---------- Сторона: Алиса -------------------
        // Верификация

        assert {
            val h = Crypto.hash(sokBob.getString())
            Crypto.verifySignature(h, sokSignatureBob, BOK)
        }

        assert {
            val h = getTransactionHash(uuid, parent_uuid, otok, bnid)
            Crypto.verifySignature(h, init_wallet_signature, sokBob)
        }

        // Алиса подписывает
        val (magic, subscribe_transaction_hash, subscribe_transaction_signature) =
            walletAlice.subscribe(uuid, parent_uuid!!, bnid)

        // Проверим, что мы не можем один и тот же parent_uuid подписать дважды
        assertThrow("Мы можем сделать 'штаны' в блокчейне!") {
            walletAlice.subscribe(uuid, parent_uuid, bnid)
        }

        // ----------- Канал связи --------------------
        // Алиса передаёт Бобу: magic, subscribe_transaction_hash, subscribe_transaction_signature

        // ---------- Сторона: Боб -------------------
        // Боб записывает блок

        return OneBlock(
            uuid = uuid,
            parentUuid = parent_uuid,
            bnid = bnid,
            otok = otok,
            magic = magic,
            subscribeTransactionHash = subscribe_transaction_hash,
            subscribeTransactionSignature = subscribe_transaction_signature,
        )
    }

    private fun validateBlockchain() = blockchain.forEachIndexed { i, block ->
        assert { block.bnid == banknote500.bnid }

        val publicKey = if (i == 0) {
            assertTrue(
                "В первом блоке parent_uuid должен быть пустым",
                block.parentUuid == null
            )
            BOK
        } else blockchain[i - 1].otok

        val transactionHash = getSubscribeTransactionHash(block.uuid, block.magic, banknote500.bnid)

        assertArrayEquals(transactionHash, block.subscribeTransactionHash)

        assert {
            Crypto.verifySignature(transactionHash, block.subscribeTransactionSignature, publicKey)
        }

        println("Блок №${1 + i} корректен!")
    }

    private fun sok_signature(sok: PublicKey): String {
        // В нормальном решении SOK должен изначально быть подписанным внутри SIM карты
        val h = Crypto.hash(sok.getString())
        return Crypto.signature(h, BPK)
    }

    private fun assert(block: () -> Boolean) {
        assert(block())
    }

    private fun assertThrow(msg: String, block: () -> Any) {
        val res = runCatching { block() }

        if (res.isSuccess)
            throw AssertionError(msg)
    }
}
