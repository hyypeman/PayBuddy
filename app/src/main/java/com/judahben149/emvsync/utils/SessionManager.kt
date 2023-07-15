package com.judahben149.emvsync.utils

import android.content.SharedPreferences
import com.judahben149.emvsync.utils.Constants.COUNTRY_CODE
import com.judahben149.emvsync.utils.Constants.CURRENCY_CODE
import com.judahben149.emvsync.utils.Constants.MERCHANT_ID
import com.judahben149.emvsync.utils.Constants.TERMINAL_ID
import javax.inject.Inject

class SessionManager @Inject constructor(private val sharedPreferences: SharedPreferences) {

    fun getCurrencyCode(): String {
        return sharedPreferences.fetchString(CURRENCY_CODE).toString()
    }

    fun getTerminalId(): String {
        return sharedPreferences.fetchString(TERMINAL_ID).toString()
    }

    fun getMerchantId(): String {
        return sharedPreferences.fetchString(MERCHANT_ID).toString()
    }

    fun getCountryCode(): String {
        return sharedPreferences.fetchString(COUNTRY_CODE).toString()
    }

}