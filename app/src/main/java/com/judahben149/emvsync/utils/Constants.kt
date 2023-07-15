package com.judahben149.emvsync.utils

object Constants {

    const val SETTINGS = "SETTINGS"

    const val IP_ADDRESS = "196.6.103.18"
    const val PORT = "5001"
    const val TERMINAL_ID = "2070AL32"
    const val COMPONENT_KEY_1 = ""
    const val COMPONENT_KEY_2 = ""
    const val IS_SSL_BOOL = false
    const val TIMEOUT = 1000

    const val TMK_PROCESSING_CODE = "9A0000"
    const val TSK_PROCESSING_CODE = "9B0000"
    const val TPK_PROCESSING_CODE = "9G0000"
    const val DOWNLOAD_PARAM_PROCESSING_CODE = "9C0000"

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