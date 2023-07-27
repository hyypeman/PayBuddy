package com.judahben149.emvsync.utils

object Constants {

    const val SETTINGS = "SETTINGS"
    const val IS_FIRST_LAUNCH = "IS_FIRST_LAUNCH"

    const val PROGRESS_DIALOG = "PROGRESS_DIALOG"
    const val CONFIRMATION_DIALOG = "CONFIRMATION_DIALOG"
    const val PURCHASE_AMOUNT = "PURCHASE_AMOUNT"
    const val IS_PURCHASE_TRANSACTION = "IS_PURCHASE_TRANSACTION"
    const val LAST_BALANCE = "LAST_BALANCE"
    const val CARD_HOLDER_NAME = "CARD_HOLDER_NAME"


    //ACQUIRER
//    const val ACQUIRING_INSTITUTION_ID = "111129"
    const val ACQUIRING_INSTITUTION_ID = "ACQUIRING_INSTITUTION_ID"

    //Channel settings constants
//    const val IP_ADDRESS = "196.6.103.18"
//    const val PORT = "5001"
//    const val TERMINAL_ID = "2070AL32"
    const val IP_ADDRESS = "IP_ADDRESS"
    const val PORT = "PORT"
    const val TERMINAL_ID = "TERMINAL_ID"
    const val COMPONENT_KEY_1 = ""
    const val COMPONENT_KEY_2 = ""
    const val IS_SSL_BOOL = false
    const val TIMEOUT = 1000


    //ISO Message constants
    const val TMK_PROCESSING_CODE = "9A0000"
    const val TSK_PROCESSING_CODE = "9B0000"
    const val TPK_PROCESSING_CODE = "9G0000"
    const val DOWNLOAD_PARAM_PROCESSING_CODE = "9C0000"

    const val BALANCE_MTI = "0100"
    const val PURCHASE_MTI = "0200"
    const val REVERSAL_MTI = "0420"
    const val BALANCE_PROCESSING_CODE = "310000"
    const val PURCHASE_PROCESSING_CODE = "000000"
//    const val reversalProcessingCode = "0100"

    const val MESSAGE_REASON_TIMEOUT = "4021"

    //ISO Response Codes
    const val SUCCESSFUL = "00"
    const val TIME_OUT = "-1"
    const val MALFORMED_RESPONSE = "-2"
    const val HOST_DISCONNECT = "-3"
    const val ERROR_PACKAGING_MESSAGE = "-4"

    const val SIXTY_FOUR_ZEROS = "0000000000000000000000000000000000000000000000000000000000000000"

    //Shared prefs keys
    const val TERMINAL_MASTER_KEY = "TERMINAL_MASTER_KEY"
    const val TERMINAL_SESSION_KEY = "TERMINAL_SESSION_KEY"

    const val TERMINAL_PIN_KEY = "TERMINAL_PIN_KEY"

    const val MERCHANT_ID = "MERCHANT_ID"
    const val MERCHANT_CATEGORY_CODE = "MERCHANT_CATEGORY_CODE"
    const val MERCHANT_LOCATION = "MERCHANT_LOCATION"
    const val CURRENCY_CODE = "CURRENCY_CODE"
    const val COUNTRY_CODE = "COUNTRY_CODE"
    const val CTMS_TIME_DATE = "CTMS_TIME_DATE"

    //EMV Constants
    const val TERMINAL_CAPABILITY_CVM = "E0F8C8" // Tag : 9F33  // Signature Required
    const val TERMINAL_CAPABILITY_NO_CVM = "E0DOC8" // Tag : 9F33
    const val TERMINAL_CAPABILITY_CVM_ONLY = "E020C8" // Tag: 9F33 // Signature only
    const val ADDITIONAL_TERMINAL_CAPABILITY = "7F00F0F001" // Tag : 9F40  or // 6000F0A001

}