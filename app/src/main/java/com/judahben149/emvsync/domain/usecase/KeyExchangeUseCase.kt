package com.judahben149.emvsync.domain.usecase

import android.content.SharedPreferences
import com.judahben149.emvsync.domain.model.NIBSSPackager
import com.judahben149.emvsync.domain.model.keyExchange.KeyExchangeResponse
import com.judahben149.emvsync.domain.model.keyExchange.KeyExchangeType
import com.judahben149.emvsync.iso.TransactionBaseChannel
import com.judahben149.emvsync.utils.*
import com.judahben149.emvsync.utils.Constants.COUNTRY_CODE
import com.judahben149.emvsync.utils.Constants.CTMS_TIME_DATE
import com.judahben149.emvsync.utils.Constants.CURRENCY_CODE
import com.judahben149.emvsync.utils.Constants.DOWNLOAD_PARAM_PROCESSING_CODE
import com.judahben149.emvsync.utils.Constants.MERCHANT_CATEGORY_CODE
import com.judahben149.emvsync.utils.Constants.MERCHANT_ID
import com.judahben149.emvsync.utils.Constants.MERCHANT_LOCATION
import com.judahben149.emvsync.utils.Constants.SIXTY_FOUR_ZEROS
import com.judahben149.emvsync.utils.Constants.TERMINAL_ID
import com.judahben149.emvsync.utils.Constants.TERMINAL_MASTER_KEY
import com.judahben149.emvsync.utils.Constants.TERMINAL_PIN_KEY
import com.judahben149.emvsync.utils.Constants.TERMINAL_SESSION_KEY
import com.judahben149.emvsync.utils.Constants.TMK_PROCESSING_CODE
import com.judahben149.emvsync.utils.Constants.TPK_PROCESSING_CODE
import com.judahben149.emvsync.utils.Constants.TSK_PROCESSING_CODE
import com.judahben149.emvsync.utils.isoUtils.ISOUtils.getStan
import com.judahben149.emvsync.utils.isoUtils.ISOUtils.parseResponse
import com.judahben149.emvsync.utils.cryptographyUtils.Sha256Utils
import com.judahben149.emvsync.utils.isoUtils.ISOUtils
import org.jpos.iso.ISODate
import org.jpos.iso.ISOException
import org.jpos.iso.ISOMsg
import org.jpos.iso.ISOUtil
import java.io.IOException
import java.util.*
import javax.inject.Inject

class KeyExchangeUseCase @Inject constructor(private val sharedPreferences: SharedPreferences, private val sessionManager: SessionManager) {

    private val date = Date()
    private val transactionPackager: NIBSSPackager = NIBSSPackager()
    private val transactionDate = ISODate.getDate(date)
    private val transactionTime = ISODate.getTime(date)
    private val transactionDateTime = ISODate.getDateTime(date)
    private var sessionKey: String? = null


    fun doTMKTransaction(channel: TransactionBaseChannel): KeyExchangeResponse {
        var result = ""

        try {
            channel.connect()
            val tmkRequest = ISOMsg()

            tmkRequest.packager = transactionPackager
            tmkRequest.mti = "0800"
            tmkRequest.set(3, TMK_PROCESSING_CODE)
            tmkRequest.set(7, transactionDateTime)
            tmkRequest.set(11, getStan())
            tmkRequest.set(12, transactionTime)
            tmkRequest.set(13, transactionDate)
            tmkRequest.set(41, sessionManager.getTerminalId())
            parseResponse(String(tmkRequest.pack()), transactionPackager)

            channel.send(tmkRequest)
            val response = channel.receive()
            channel.disconnect()

            parseResponse(String(response.pack()), transactionPackager)

            if (response.getString(39).endsWith("00")) {
                val masterKey = ISOUtils.getDecryptedTMKFromHost(response.getString(53)).toString()
                "Master Key - $masterKey".logThis()

                result = response.getString(39)
                sharedPreferences.saveString(TERMINAL_MASTER_KEY, masterKey)
            } else {
                response.getString(39).toString().logThis()
                result = response.getString(39)
            }

        } catch (e: ISOException) {
            "Key Exchange (TMK) Error- ".plus(e.message).logThis()
            result = "-1"
        } catch (e: IOException) {
            "Key Exchange (TMK) Error- ".plus(e.message).logThis()
            result = "-1"
        }

        return KeyExchangeResponse(KeyExchangeType.TMK, result)
    }

    fun doTSKTransaction(channel: TransactionBaseChannel): KeyExchangeResponse {
        var result = ""

        try {
            channel.connect()
            val tskRequest = ISOMsg()

            tskRequest.packager = transactionPackager
            tskRequest.mti = "0800"
            tskRequest.set(3, TSK_PROCESSING_CODE)
            tskRequest.set(7, transactionDateTime)
            tskRequest.set(11, getStan())
            tskRequest.set(12, transactionTime)
            tskRequest.set(13, transactionDate)
            tskRequest.set(41, sessionManager.getTerminalId())
            parseResponse(String(tskRequest.pack()), transactionPackager)

            channel.send(tskRequest)
            val response = channel.receive()
            channel.disconnect()

            parseResponse(String(response.pack()), transactionPackager)

            if (response.getString(39).endsWith("00")) {
                val savedMasterKey = sharedPreferences.fetchString(TERMINAL_MASTER_KEY)
                val sessionKey = ISOUtils.getDecryptedTSKFromHost(response.getString(53), savedMasterKey).toString()

                result = response.getString(39)
                sharedPreferences.saveString(TERMINAL_SESSION_KEY, sessionKey)
            } else {
                response.getString(39).toString().logThis()
                result = response.getString(39)
            }

        } catch (e: ISOException) {
            "Key Exchange (TSK) Error- ".plus(e.message).logThis()
            result = "-1"
        } catch (e: IOException) {
            "Key Exchange (TSK) Error- ".plus(e.message).logThis()
            result = "-1"
        }

        return KeyExchangeResponse(KeyExchangeType.TSK, result)
    }

    fun doTPKTransaction(channel: TransactionBaseChannel): KeyExchangeResponse {
        var result = ""

        try {
            channel.connect()
            val tpkRequest = ISOMsg()

            tpkRequest.packager = transactionPackager
            tpkRequest.mti = "0800"
            tpkRequest.set(3, TPK_PROCESSING_CODE)
            tpkRequest.set(7, transactionDateTime)
            tpkRequest.set(11, getStan())
            tpkRequest.set(12, transactionTime)
            tpkRequest.set(13, transactionDate)
            tpkRequest.set(41, sessionManager.getTerminalId())
            parseResponse(String(tpkRequest.pack()), transactionPackager)

            channel.send(tpkRequest)
            val response = channel.receive()
            channel.disconnect()

            parseResponse(String(response.pack()), transactionPackager)

            if (response.getString(39).endsWith("00")) {
                val pinKey = response.getString(53).substring(0, 32)
                "Pin Key - $pinKey".logThis()

                sharedPreferences.saveString(TERMINAL_PIN_KEY, pinKey)
                result = response.getString(39)
            } else {
                response.getString(39).toString().logThis()
                result = response.getString(39)
            }

        } catch (e: ISOException) {
            "Key Exchange (TPK) Error- ".plus(e.message).logThis()
            result = "-1"
        } catch (e: IOException) {
            "Key Exchange (TPK) Error- ".plus(e.message).logThis()
            result = "-1"
        }

        return KeyExchangeResponse(KeyExchangeType.TSK, result)
    }


    fun doParameterDownloadTransaction(channel: TransactionBaseChannel): KeyExchangeResponse {
        var result = ""

        try {
            channel.connect()
            val parameterDownloadRequest = ISOMsg()

            parameterDownloadRequest.packager = transactionPackager
            parameterDownloadRequest.mti = "0800"
            parameterDownloadRequest.set(3, DOWNLOAD_PARAM_PROCESSING_CODE)
            parameterDownloadRequest.set(7, transactionDateTime)
            parameterDownloadRequest.set(11, getStan())
            parameterDownloadRequest.set(12, transactionTime)
            parameterDownloadRequest.set(13, transactionDate)
            parameterDownloadRequest.set(41, sessionManager.getTerminalId())
            parameterDownloadRequest.set(62, "01008".plus(TERMINAL_ID))

            parameterDownloadRequest.set(64, ISOUtil.hex2byte(SIXTY_FOUR_ZEROS))
            parameterDownloadRequest.recalcBitMap()

            val prePack = parameterDownloadRequest.pack()
            val sessionKey = sharedPreferences.fetchString(TERMINAL_SESSION_KEY)

            parameterDownloadRequest.set(
                64,
                Sha256Utils.performSha256Hash(
                    ISOUtil.trim(prePack, prePack.size - 64),
                    ISOUtil.hex2byte(sessionKey)
                )
            )

            parseResponse(String(parameterDownloadRequest.pack()), transactionPackager)

            channel.send(parameterDownloadRequest)
            val response = channel.receive()
            channel.disconnect()

            parseResponse(String(response.pack()), transactionPackager)

            if (response.getString(39).endsWith("00")) {
                val field62 = response.getString(62)

                val merchantId = ISOUtils.parseTLV(field62, "03").toString()
                val merchantCategoryCode = ISOUtils.parseTLV(field62, "08").toString()
                val merchantLocation = ISOUtils.parseTLV(field62, "52").toString()
                val currencyCode = ISOUtils.parseTLV(field62, "05").toString()
                val countryCode = ISOUtils.parseTLV(field62, "06").toString()
                val ctmsTimeDate = ISOUtils.parseTLV(field62, "02").toString()

                sharedPreferences.apply {
                    saveString(MERCHANT_ID, merchantId)
                    saveString(MERCHANT_CATEGORY_CODE, merchantCategoryCode)
                    saveString(MERCHANT_LOCATION, merchantLocation)
                    saveString(CURRENCY_CODE, currencyCode)
                    saveString(COUNTRY_CODE, countryCode)
                    saveString(CTMS_TIME_DATE, ctmsTimeDate)
                }

                result = response.getString(39)
            }
            else {
                "Field 39 not 00 Error- ".logThis()
                result = "-1"
            }
        } catch (ex: ISOException) {
            "Key Exchange (Parameter download) Error- ".plus(ex.message).logThis()
            result = "-1"
        }

        return KeyExchangeResponse(KeyExchangeType.TPD, result)
    }
}