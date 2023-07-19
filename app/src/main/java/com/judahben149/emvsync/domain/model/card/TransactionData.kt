package com.judahben149.emvsync.domain.model.card

data class TransactionData(

    val cardNo: String?= null,
    val expiryDate: String? = null,
    val cardSerialNo: String? = null,
    val track2Data: String? = null,
    val amount: Double = 0.0,
    val iccData: String? = null,
    val aid: String? = null,
    val rrn: String? = null,
    val stan: String?= null,
    val datetime: String? = null,
    val pinData: String? = null,
    val transDate: String? = null,
    val transTime: String? = null,
    val appLabel: String? = null,
    val originalAmount: String? = null,
    val processingCode: String? = null,
    val accountType: String? = null,
    val cardHolderName: String? = null,
    val transDateTime: String? = null,
    val serviceCode: String? = null,
    val pinType: String? = null,
    val transType: TransactionType = TransactionType.BALANCE,
    val contactlessMode: String? = ContactlessModeConstant.EMV.name,
    val cardType: String? = null,
    val accountBalance: String? = null,
    val isOnlinePin: Boolean? = false,
    val posEntryMode: String? = null,
    val authCode: String? = null,
    val echoData: String? = null,
    val tvr: String? = null,
    val tsi: String? = null,
    val responseMessage: String? = null,
    val responseCode: String? = null,
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

enum class ContactlessModeConstant {
    MSD,
    EMV
}