package com.judahben149.emvsync.domain.model.keyExchange

data class KeyExchangeResponse(
    val type: KeyExchangeType,
    val result: String
) {

    fun isSuccessful(): Boolean {
        return result.endsWith("00")
    }

    fun getMessage(): String {
        return if (isSuccessful()) {
            type.name.plus(" Successful")
        } else {
            type.name.plus(" Failed with Error - $result")
        }
    }
}

enum class KeyExchangeType {
    TMK,
    TPK,
    TSK,
    TPD,
}