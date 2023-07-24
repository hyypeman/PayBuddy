package com.judahben149.emvsync.utils.constants

object EmvConstants {


}

object PosEntryMode {
    const val UNSPECIFIED = "00"
    const val MANUAL = "01"
    const val MAGNETIC_STRIPE_CVV_UNRELIABLE = "02"
    const val BAR_CODE = "03"
    const val OCR = "04"
    const val INTEGRATED_CIRCUIT_CARD = "05"
    const val CONTACTLESS_INTEGRATED_CIRCUIT_CARD = "07"
    const val FALLBACK = "80"
    const val MAGNETIC_STRIPE = "90"
    const val CONTACTLESS_MAGNETIC_STRIPE = "91"
    const val INTEGRATED_CIRCUIT_CARD_CVV_UNRELIABLE = "95"
    const val AME_AS_ORIGINAL_TRANSACTION = "99"
}