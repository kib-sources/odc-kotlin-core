/*

 */

package core.crypto

import java.nio.charset.StandardCharsets
import java.security.*
import java.util.*
import java.security.KeyFactory
import java.security.spec.X509EncodedKeySpec
import java.security.spec.PKCS8EncodedKeySpec

import java.security.PublicKey


object Crypto {
    fun initPair(): Pair<PublicKey, PrivateKey> {
        val pairGenerator = KeyPairGenerator.getInstance("RSA")
        pairGenerator.initialize(512)
        val pair = pairGenerator.genKeyPair()
        return pair.public to pair.private
    }

    fun hash(vararg strings: String?): ByteArray {
        val salt = "eRgjPi235ps1"
        var v = salt

        for (value in strings.filterNotNull()) {
            v += "|$value"
        }

        return v.sha256()
    }

    fun signature(hexHash: ByteArray, privateKey: PrivateKey): String {
        val privateSignature: Signature = Signature.getInstance("SHA256withRSA")
        privateSignature.initSign(privateKey)
        privateSignature.update(hexHash)

        val signature: ByteArray = privateSignature.sign()
        return Base64.getEncoder().encodeToString(signature)
    }

    fun verifySignature(hexHash: ByteArray, signature: String, publicKey: PublicKey): Boolean {
        val publicSignature = Signature.getInstance("SHA256withRSA")
        publicSignature.initVerify(publicKey)
        publicSignature.update(hexHash)

        val signatureBytes = Base64.getDecoder().decode(signature)

        return publicSignature.verify(signatureBytes)
    }

    private fun String.sha256(): ByteArray = MessageDigest
            .getInstance("SHA-256")
            .digest(toByteArray())

    fun ByteArray.toHex(): String = joinToString("") { "%02x".format(it) }
}

fun loadPrivateKey(stored: String): PrivateKey {
    val keySpec = PKCS8EncodedKeySpec(Base64.getDecoder().decode(stored.toByteArray(StandardCharsets.UTF_8)))
    val keyFactory = KeyFactory.getInstance("RSA")
    return keyFactory.generatePrivate(keySpec)
}

fun loadPublicKey(stored: String): PublicKey {
    val data = Base64.getDecoder().decode(stored.toByteArray(StandardCharsets.UTF_8))
    val spec = X509EncodedKeySpec(data)
    val keyFactory = KeyFactory.getInstance("RSA")
    return keyFactory.generatePublic(spec)
}

fun PublicKey.getString(): String {
    val keyFactory = KeyFactory.getInstance("RSA")
    val spec = keyFactory.getKeySpec(this, X509EncodedKeySpec::class.java)
    return Base64.getEncoder().encodeToString(spec.encoded)
}


fun PrivateKey.getString(): String {
    val spec = PKCS8EncodedKeySpec(encoded)
    return Base64.getEncoder().encodeToString(spec.encoded)
}