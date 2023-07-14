package com.judahben149.emvsync.utils.isoUtils

import org.jpos.iso.ISOCurrency

object AmountUtils {

    fun toIsoAmount(amount: Double, currencyCode: String): String {
        if (amount == 0.0 || currencyCode.isNullOrEmpty())
            return ""

        return ISOCurrency.convertToIsoMsg(amount, currencyCode)
    }
}