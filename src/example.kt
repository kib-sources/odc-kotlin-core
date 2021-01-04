
import core.crypto.Crypto

fun checkExample(){
    // println("Hello Kotlin 4!!")
    val (publicKey, privateKey) = Crypto.initPair()
    println(publicKey)
    println(privateKey)


}

fun main(args: Array<String>){
    println("Example application in core")
    checkExample()
}