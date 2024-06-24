package com.judahben149.emvsync.utils

import android.content.SharedPreferences
import com.judahben149.emvsync.utils.Constants.ACQUIRING_INSTITUTION_ID
import com.judahben149.emvsync.utils.Constants.COUNTRY_CODE
import com.judahben149.emvsync.utils.Constants.CURRENCY_CODE
import com.judahben149.emvsync.utils.Constants.HOST_URL
import com.judahben149.emvsync.utils.Constants.IP_ADDRESS
import com.judahben149.emvsync.utils.Constants.MERCHANT_CATEGORY_CODE
import com.judahben149.emvsync.utils.Constants.MERCHANT_ID
import com.judahben149.emvsync.utils.Constants.MERCHANT_LOCATION
import com.judahben149.emvsync.utils.Constants.PORT
import com.judahben149.emvsync.utils.Constants.TERMINAL_ID
import com.judahben149.emvsync.utils.Constants.TERMINAL_SESSION_KEY
import javax.inject.Inject

class SessionManager @Inject constructor(private val sharedPreferences: SharedPreferences) {

    fun getCurrencyCode(): String {
        return sharedPreferences.fetchString(CURRENCY_CODE).toString()
    }

    fun getTerminalId(): String {
        return sharedPreferences.fetchString(TERMINAL_ID).toString()
//        return Constants.TERMINAL_ID
    }

    fun getAcquiringInstitutionCode(): String {
        return sharedPreferences.fetchString(ACQUIRING_INSTITUTION_ID).toString()
//        return Constants.ACQUIRING_INSTITUTION_ID
    }

    fun getHostURL(): String {
        return sharedPreferences.fetchString(HOST_URL).toString()
    }

    fun getMerchantId(): String {
        return sharedPreferences.fetchString(MERCHANT_ID).toString()
    }

    fun getCountryCode(): String {
        return sharedPreferences.fetchString(COUNTRY_CODE).toString()
    }

    fun getMerchantCategoryCode(): String {
        return sharedPreferences.fetchString(MERCHANT_CATEGORY_CODE).toString()
    }

    fun getMerchantNameAndLocation(): String {
        return sharedPreferences.fetchString(MERCHANT_LOCATION).toString()
    }

    fun getSessionKey(): String {
        return sharedPreferences.fetchString(TERMINAL_SESSION_KEY).toString()
    }

    fun saveHostIpAddress(ipAddress: String) {
        sharedPreferences.saveString(IP_ADDRESS, ipAddress)
    }

    fun savePort(port: String) {
        sharedPreferences.saveString(PORT, port)
    }

    fun saveTerminalNumber(terminalNumber: String) {
        sharedPreferences.saveString(TERMINAL_ID, terminalNumber)
    }

    fun saveAcquirerID(acquirerId: String) {
        sharedPreferences.saveString(ACQUIRING_INSTITUTION_ID, acquirerId)
    }
}