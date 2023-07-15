package com.judahben149.emvsync.domain.model.card

data class TransactionData(

    var cardNo: String?= null,
    var expiryDate: String? = null,
    var cardSerialNo: String? = null,
    var track2Data: String? = null,
    var amount: Double = 0.0,
    var iccData: String? = null,
    var aid: String? = null,
    var rrn: String? = null,
    var stan: String?= null,
    var datetime: String? = null,
    var pinData: String? = null,
    var transDate: String? = null,
    var transTime: String? = null,
    var appLabel: String? = null,
    val originalAmount: String? = null,
    val processingCode: String? = null,
    var accountType: String? = null,
    var cardHolderName: String? = null,
    val transDateTime: String? = null,
    var serviceCode: String? = null,
    var pinType: String? = null,
    var transType: String? = null,
    var contactlessMode: String? = ContactlessModeConstant.EMV.name,
    var cardType: String? = null,
    var accountBalance: String? = null,
    val isOnlinePin: Boolean? = false,
    var posEntryMode: String? = null,
    var authCode: String? = null,
    var echoData: String? = null,
    var tvr: String? = null,
    var tsi: String? = null,
    var responseMessage: String? = null,
    var responseCode: String? = null
)

data class TransactionType(
    val transactionTypeCode: String = "",
    val messageType: String = "",
    val transactionTypeLabel: String = "",
)


enum class ContactlessModeConstant {
    MSD,
    EMV
}