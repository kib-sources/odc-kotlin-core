import npo.kib.odc.crypto.*
import npo.kib.odc.crypto.Crypto.toHex
import org.junit.Test
import org.junit.Assert.*

class CryptoTest {
    @Test
    fun basic_test() {
        val (publicKey, privateKey) = Crypto.initPair()

        val hexHash: ByteArray = Crypto.hash("Привет", "Мир")
        assertEquals(
            "5a5248ffd265ed5fabbaa81cd36cfd3646023278007e8bca80cbf042b44242b7",
            hexHash.toHex()
        )

        val signature: String = Crypto.signature(hexHash, privateKey)
        assertTrue(Crypto.verifySignature(hexHash, signature, publicKey))
    }

    @Test
    fun hash_test() {
        val hexHash1: ByteArray = Crypto.hash("Привет", "Мир")
        val hexHash2: ByteArray = Crypto.hash("Привет", null, "Мир")

        assertEquals(hexHash1.toHex(), hexHash2.toHex())
    }

    @Test
    fun rsa_test() {
        val (publicKey, privateKey) = Crypto.initPair()

        val publicKeyStr = publicKey.getString()
        val publicKeyLoaded = loadPublicKey(publicKeyStr)
        assertEquals(publicKey, publicKeyLoaded)

        val privateKeyStr = privateKey.getString()
        val privateKeyLoaded = loadPrivateKey(privateKeyStr)
        assertEquals(privateKey, privateKeyLoaded)
    }
}