/*

 */

package core.data


data class Banknote(
        val bin: Int,

        val amount: Int,

        val currencyCode: Int,

        // BankNote id
        val bnid: String,

        // hash
        // val hash: ByteArray,
        val hashValue: ByteArray,
        val signature: String,
) {



}