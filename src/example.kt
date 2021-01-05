
import core.crypto.Crypto
import core.data.ProtectedBlock
import core.data.Block
import core.issuer.BankIssuer


import core.wallet.Wallet

import core.enums.ISO_4217_CODE
import java.lang.Exception

fun checkExample(){
    // println("Hello Kotlin 4!!")
    val (publicKey, privateKey) = Crypto.initPair()
    println(publicKey)
    println(privateKey)

    println(ISO_4217_CODE.AFN)
    println(ISO_4217_CODE.ALL)

}

fun example1(){

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

    // Банк по сети передает: banknote500

    // Шаг 2 Верификация банкноты
    if ( ! banknote500.verification(bok)){
        throw Exception("Банкнота поддельная")
    }

    // Шаг 3. Алиса создаёт новый блок
    var (block, protectedBlock) = walletA.firstBlock(banknote500)

    // Передача по каналу от Алисы к банку: block, protectedBlock

    // Шаг 4. Банк подписывает банкноту

    block = bankIssuer.signature(banknote500, block, protectedBlock)

    // Шаг 5а верификация
    if ( ! block.verification(bok)){
        throw Exception("Некорректный блок блокчейна")
    }

    // Шаг 5b. Задержка на всякий случай (вдруг что-либо забыли)
    // Thread.sleep(1000L)

    // Шаг 6. LocalPush

    banknote500_blockchain.add(block)
    banknote500_protectedBlockChain.add(protectedBlock)

    // Шаг 7. Push
    bankIssuer.pushBlock(banknote500, block, protectedBlock)



    /// ---------------------------------------------------------------------------------------------------------------
    /// A -> B



}

fun main(args: Array<String>){
    println("Example application in core")
    // checkExample()
    example1()
}