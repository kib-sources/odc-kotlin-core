package core

import java.security.PublicKey
import java.util.*

data class OneBlock(
    val uuid: UUID,
    val parentUuid: UUID?,

    // BankNote id
    val bnid: String,

    // One Time Open key
    val otok: PublicKey,

    val magic: String,
    val subscribeTransactionHash: ByteArray,
    val subscribeTransactionSignature: String,
)
