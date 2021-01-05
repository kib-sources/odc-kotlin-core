
import core.crypto.Crypto
import core.data.ProtectedBlock
import core.data.Block
import core.issuer.BankIssuer


import core.wallet.Wallet

import core.enums.ISO_4217_CODE

fun checkExample(){
    // println("Hello Kotlin 4!!")
    val (publicKey, privateKey) = Crypto.initPair()
    println(publicKey)
    println(privateKey)

    println(ISO_4217_CODE.AFN)
    println(ISO_4217_CODE.ALL)

}

fun checkExampleWallet(){

    val BIN = 4274

    // Bank
    val (bok, bpk) = Crypto.initPair()
    // Alice
    val (sokA, spkA) = Crypto.initPair()
    // Bob
    val (sokB, spkB) = Crypto.initPair()

    val bankIssuer = BankIssuer(bpk, bok)

    // Bank subscribe
    val sokAsignature = bankIssuer.addWallet(sokA)
    val sokBsignature = bankIssuer.addWallet(sokB)

    val walletA = Wallet(spkA, sokA, sokAsignature, bok)
    val walletB = Wallet(spkB, sokB, sokBsignature, bok)




    /// ---------------------------------------------------------------------------------------------------------------
    /// Инициализация купюры и блокчейнов.

    val banknote500 = bankIssuer.newBanknote(
            amount = 500,
            bin = BIN,
            currencyCode=ISO_4217_CODE.RUB,
    )

    val banknote500_blockchain: MutableList<Block> = mutableListOf()
    val banknote500_protectedBlockChain: MutableList<ProtectedBlock> =  mutableListOf()



    /// ---------------------------------------------------------------------------------------------------------------
    /// Банк -> А

    // Шаг 1. Банк по сети передает банкноту

    // Шаг 2. Алиса создаёт новый блок
    val (block, protectedBlock) = walletA.firstBlock(banknote500)

    bankIssuer.



    /// ---------------------------------------------------------------------------------------------------------------
    /// A -> B



}

fun main(args: Array<String>){
    println("Example application in core")
    checkExample()
}