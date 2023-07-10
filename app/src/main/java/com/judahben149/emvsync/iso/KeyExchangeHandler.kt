package com.judahben149.emvsync.iso

import android.content.Context
import com.judahben149.emvsync.domain.model.NIBSSPackager
import com.judahben149.emvsync.utils.Constants
import com.judahben149.emvsync.utils.Constants.TMK_PROCESSING_CODE
import com.judahben149.emvsync.utils.getStan
import com.judahben149.emvsync.utils.logThis
import com.judahben149.emvsync.utils.parseResponse
import org.jpos.iso.ISODate
import org.jpos.iso.ISOException
import org.jpos.iso.ISOMsg
import java.io.IOException
import java.util.*
import javax.inject.Inject

class KeyExchangeHandler @Inject constructor() {

    private val date = Date()
    private val transactionPackager: NIBSSPackager = NIBSSPackager()
    private val transactionDate = ISODate.getDate(date)
    private val transactionTime = ISODate.getTime(date)
    private val transactionDateTime = ISODate.getDateTime(date)
    private var sessionKey: String? = null


    fun doTMKTransaction(channel: TransactionBaseChannel) {

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
            tmkRequest.set(41, Constants.TERMINAL_ID)
            parseResponse(String(tmkRequest.pack()), transactionPackager)

            channel.send(tmkRequest)
            val response = channel.receive()
            channel.disconnect()

            parseResponse(String(response.pack()), transactionPackager)

            if (response.getString(39).endsWith("00")) {

            }

        } catch (e: ISOException) {
            "Key Exchange (TMK) Error- ".plus(e.message).logThis()
        } catch (e: IOException) {
            "Key Exchange (TMK) Error- ".plus(e.message).logThis()
        }
    }
}