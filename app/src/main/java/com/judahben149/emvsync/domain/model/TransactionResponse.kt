package com.judahben149.emvsync.domain.model

data class TransactionResponse(
    var responseCode: String? = null,
    var authCode: String? = null,
    var responseMessage: String? = null,
    var iccData: String? = null,
    var accountBalance: String? = null,
)
