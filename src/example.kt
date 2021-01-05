
import core.crypto.Crypto
import core.data.Banknote
import core.data.ProtectedBlock
import core.data.Block
import core.issuer.BankIssuer


import core.wallet.Wallet

import core.enums.ISO_4217_CODE
import java.lang.Exception
import java.security.PublicKey
import java.util.*

fun checkExample(){
    // println("Hello Kotlin 4!!")
    val (publicKey, privateKey) = Crypto.initPair()
    println(publicKey)
    println(privateKey)

    println(ISO_4217_CODE.AFN)
    println(ISO_4217_CODE.ALL)

}

data class ExampleParty(
    val bankIssuer: BankIssuer,
    val walletA: Wallet,
    val walletB: Wallet,
    val BIN: Int,
    val BOK: PublicKey,
)

fun inits(): ExampleParty{

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

    return ExampleParty(bankIssuer, walletA, walletB, BIN, bok)
}

fun example1(exampleParty: ExampleParty): Triple<Banknote, MutableList<Block>, MutableList<ProtectedBlock>>{

    val bankIssuer = exampleParty.bankIssuer
    val walletA = exampleParty.walletA
    val BIN = exampleParty.BIN
    val bok = exampleParty.BOK
    /// ---------------------------------------------------------------------------------------------------------------
    /// Инициализация купюры и блокчейнов.

    val banknote = bankIssuer.newBanknote(
            amount = 500,
            bin = BIN,
            currencyCode=ISO_4217_CODE.RUB,
    )
    println("Сгенерирована банкнота на 500 рублей")

    val banknote_blockchain: MutableList<Block> = mutableListOf()
    val banknote_protectedBlockChain: MutableList<ProtectedBlock> =  mutableListOf()

    /// ---------------------------------------------------------------------------------------------------------------
    /// Банк -> А

    // Банк по сети передает: banknote

    // Шаг 2 Верификация банкноты
    if ( ! banknote.verification(bok)){
        throw Exception("Банкнота поддельная")
    }

    // Шаг 3. Алиса создаёт новый блок
    var (block, protectedBlock) = walletA.firstBlock(banknote)

    // Передача по каналу от Алисы к банку: block, protectedBlock

    // Шаг 4. Банк подписывает банкноту

    block = bankIssuer.signature(banknote, block, protectedBlock)

    // Шаг 5а верификация
    if ( ! block.verification(bok)){
        throw Exception("Некорректный блок блокчейна")
    }

    // Шаг 5b. Задержка на всякий случай (вдруг что-либо забыли)
    // Thread.sleep(1000L)

    // Шаг 6. LocalPush

    banknote_blockchain.add(block)
    banknote_protectedBlockChain.add(protectedBlock)

    // Шаг 7. Push
    bankIssuer.pushBlock(banknote, block, protectedBlock)

    println("Банкнота успешно передана Алисе")
    return Triple(banknote, banknote_blockchain, banknote_protectedBlockChain)
}

fun example2(exampleParty: ExampleParty, banknote:Banknote, banknote_blockchain:MutableList<Block>, banknote_protectedBlockChain:MutableList<ProtectedBlock>){
    /// ---------------------------------------------------------------------------------------------------------------
    /// A -> B
    val walletA = exampleParty.walletA
    val walletB = exampleParty.walletA
    val BIN = exampleParty.BIN
    val bok = exampleParty.BOK

    // А передаёт Б: banknote_blockchain

    // Шаг 1. Создание запроса на уведомление
    val parentBlock = banknote_blockchain.last()

    val protectedBlock_part = ProtectedBlock(
            parentSok=walletA.sok,
            parentSokSignature=walletA.sokSignature,
            parentOtokSignature=walletA.otokSignature(parentBlock.otok),
            refUuid=null,
            sok=null,
            sokSignature=null,
            otokSignature=null,
    )


    // TODO переименовать на схеме: blockchain verification
    // Шаг 2. Init_verification
    var lastKey = bok
    for (block in banknote_blockchain){
        block.verification(lastKey)
        lastKey = block.otok
    }

    // Шаг 3. push
    //    1. передача блокчейна по сети
    //    2. в случае возникновения конфликтов блокчейна -- об этом сообщит сервер

    // Шаг 4.

    var (childBlock, protectedBlock) = walletB.acceptanceInit(parentBlock, protectedBlock_part, bok)

    // Передача по каналу block, protectedBlock стороне А.

    // Шаг 5. Сторона А должна убедится что корректно собраны все блоки
    val exp = walletA.acceptanceInitVerification(parentBlock, childBlock, protectedBlock)
    if (exp != null){
        throw exp
    }

    //

}

fun main(args: Array<String>){
    println("Example application in core")
    // checkExample()
    
    val exampleParty = inits()
    val (banknote, banknote_blockchain, banknote_protectedBlockChain) = example1(exampleParty)
    
}