package com.judahben149.emvsync.domain.model.card

data class TransactionData(

    val cardNo: String?= null,
    var expiryDate: String? = null,
    var cardSequenceNo: String? = null,
    var track2Data: String? = null,
    val amount: Double = 0.0,
    var iccData: String? = null,
    var aid: String? = null,
    val rrn: String? = null,
    val stan: String?= null,
    val datetime: String? = null,
    val pinData: String? = null,
    val transDate: String? = null,
    val transTime: String? = null,
    var appLabel: String? = null,
    val originalAmount: String? = null,
    val processingCode: String? = null,
    val accountType: String? = null,
    var cardHolderName: String? = null,
    val transDateTime: String? = null,
    var serviceCode: String? = null,
    val pinType: PinType? = null,
    val transType: TransactionType = TransactionType.BALANCE,
    var contactlessMode: ContactlessModeConstant = ContactlessModeConstant.EMV,
    val cardType: String? = null,
    var accountBalance: String? = null,
    val isOnlinePin: Boolean? = false,
    val posEntryMode: String? = null,
    var authCode: String? = null,
    var echoData: String? = null,
    var tvr: String? = null,
    var tsi: String? = null,
    var responseMessage: String? = null,
    var responseCode: String? = null,
    val pinEnteredLength: Int = 0
)

//data class TransactionType(
//    val transactionTypeCode: String = "",
//    val messageType: String = "",
//    val transactionTypeLabel: String = "",
//)

enum class TransactionType {
    BALANCE,
    PURCHASE,
    REVERSAL
}

enum class PinType {
    ONLINE_CVM,
    OFFLINE_CVM
}

enum class ContactlessModeConstant {
    MSD,
    EMV
}