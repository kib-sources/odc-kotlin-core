package examples

import core.Banknote
import org.junit.Test
import org.junit.Assert.*
import java.time.LocalDateTime

class FirstUseCase {
    @Test
    fun useCase() {
        println(LocalDateTime.now())

        val banknote500 = Banknote.init(bpk=BPK, bin=BIN, amount=500)
        assertTrue(banknote500.verify(bok=BOK))

        val banknote100 = Banknote.init(bpk=BPK, bin=BIN, amount=100)
        assertTrue(banknote100.verify(bok=BOK))
    }
}