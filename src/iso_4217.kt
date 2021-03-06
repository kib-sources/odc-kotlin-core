/*
  COUNTRY CURRENCY CODES

  Updated: June 18, 2019

  https://www.iban.com/currency-codes
 */
package core.enums


enum class ISO_4217_CODE(private val code:Int){
    // AFGHANISTAN, Afghani
    AFN(971),

    // ALBANIA, Lek
    ALL(8),

    // ALGERIA, Algerian Dinar
    DZD(12),

    // ...

    // RUSSIAN FEDERATION, Russian Ruble
    RUB (643),

    // UNITED STATES OF AMERICA, US Dollar
    USD (840),


    // Euro
    EUR(978);

    // TODO toString 8 -> "008", 12 -> "012"
    override fun toString(): String {
        if (this.code < 10){
            return "00" + this.code.toString()
        }
        if (this.code < 100){
            return "0" + this.code.toString()
        }
        return this.code.toString()

    }
}