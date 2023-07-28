package com.judahben149.emvsync.dummy

data class RequestData(
    val amount: String? = "0.0",
    val cbAmount: String? = null,
    val transType: String? = "BALANCE",
    val transactionReference: String? = null,
    val schemeType: String? = null,
    val action: String? = null,
    val print: String? = null,
    val stan: String? = "765890",
    val rrn: String? = "34566787656"

) {

    fun hasRrnAndStan(): Boolean {
        return rrn != null && rrn.trim { it <= ' ' } != "" && stan != null && stan.trim { it <= ' ' } != ""
    }
}
