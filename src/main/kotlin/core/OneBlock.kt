package core

data class OneBlock(
    val uuid: String,
    val parent_uuid: String,

    // BankNote id
    val bnid: String,

    // One Time Open key
    val otok: String,

    val magic: String,
    val subscribe_transaction_hash: String,
    val subscribe_transaction_signature: String,
)
