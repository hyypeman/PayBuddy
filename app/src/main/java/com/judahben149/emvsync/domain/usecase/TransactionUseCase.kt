package com.judahben149.emvsync.domain.usecase

import com.judahben149.emvsync.domain.model.TransactionResponse
import com.judahben149.emvsync.domain.model.NIBSSPackager
import com.judahben149.emvsync.domain.model.card.PinType
import com.judahben149.emvsync.domain.model.card.TransactionData
import com.judahben149.emvsync.domain.model.card.TransactionType
import com.judahben149.emvsync.iso.TransactionBaseChannel
import com.judahben149.emvsync.utils.Constants.BALANCE_MTI
import com.judahben149.emvsync.utils.Constants.BALANCE_PROCESSING_CODE
import com.judahben149.emvsync.utils.Constants.ERROR_PACKAGING_MESSAGE
import com.judahben149.emvsync.utils.Constants.HOST_DISCONNECT
import com.judahben149.emvsync.utils.Constants.MESSAGE_REASON_TIMEOUT
import com.judahben149.emvsync.utils.Constants.PURCHASE_MTI
import com.judahben149.emvsync.utils.Constants.PURCHASE_PROCESSING_CODE
import com.judahben149.emvsync.utils.Constants.REVERSAL_MTI
import com.judahben149.emvsync.utils.Constants.SIXTY_FOUR_ZEROS
import com.judahben149.emvsync.utils.Constants.TIME_OUT
import com.judahben149.emvsync.utils.isoUtils.ISOUtils
import com.judahben149.emvsync.utils.SessionManager
import com.judahben149.emvsync.utils.cryptographyUtils.Sha256Utils
import com.judahben149.emvsync.utils.isoUtils.AmountUtils
import com.judahben149.emvsync.utils.isoUtils.IsoResponseUtils
import com.judahben149.emvsync.utils.logThis
import com.nexgo.common.ByteUtils
import org.jpos.iso.ISOException
import org.jpos.iso.ISOMsg
import org.jpos.iso.ISOUtil
import java.io.EOFException
import java.io.IOException
import javax.inject.Inject

class TransactionUseCase @Inject constructor(
    private val sessionManager: SessionManager
) {

    private val transactionPackager: NIBSSPackager = NIBSSPackager()


    fun buildISOMsg(channel: TransactionBaseChannel, transactionData: TransactionData, updateBalanceResponse:(TransactionResponse) -> Unit): ISOMsg {

        var isoResponse = ISOMsg()

        try {
            channel.connect()
            val isoRequest = ISOMsg()
            isoRequest.packager = transactionPackager

            when (transactionData.transType) {
                TransactionType.BALANCE -> {
                    isoRequest.set(0, BALANCE_MTI)
                    isoRequest.set(3, BALANCE_PROCESSING_CODE)
                }
                TransactionType.REVERSAL -> {
                    isoRequest.set(0, REVERSAL_MTI)
                    isoRequest.set(3, PURCHASE_PROCESSING_CODE)
                    isoRequest.set(56, MESSAGE_REASON_TIMEOUT) //Message Reason Code - mandatory for reversal transactions
                    isoRequest.set(38, transactionData.authCode)
                    isoRequest.set(95, AmountUtils.toIsoAmount(transactionData.amount, sessionManager.getCurrencyCode()) + "000000000000" + "D00000000D00000000")
                }
                TransactionType.PURCHASE -> {
                    isoRequest.set(0, PURCHASE_MTI)
                    isoRequest.set(3, PURCHASE_PROCESSING_CODE)
                }
            }

            isoRequest.set(2, ISOUtil.trimf(transactionData.cardNo))
            isoRequest.set(4, AmountUtils.toIsoAmount(transactionData.amount, sessionManager.getCurrencyCode()))

            isoRequest.set(7, transactionData.datetime)
            isoRequest.set(12, transactionData.transTime)
            isoRequest.set(13, transactionData.transDate)
            isoRequest.set(37, transactionData.rrn)
            isoRequest.set(11, transactionData.stan)
            isoRequest.set(14, transactionData.expiryDate)
            isoRequest.set(18, sessionManager.getMerchantCategoryCode())

            isoRequest.set(22, transactionData.posEntryMode + 1)
            isoRequest.set(25, "00") //POS condition code
            isoRequest.set(28, "D00000000") //Amount,  transaction fee

            isoRequest.set(32, sessionManager.getAcquiringInstitutionCode())
            isoRequest.set(35, ISOUtil.trimf(transactionData.track2Data))

            transactionData.serviceCode?.let {
                isoRequest.set(40, ISOUtil.trimf(it))
            }

            isoRequest.set(41, sessionManager.getTerminalId())
            isoRequest.set(42, sessionManager.getMerchantId())
            isoRequest.set(43, sessionManager.getMerchantNameAndLocation())
            isoRequest.set(49, sessionManager.getCurrencyCode())

            if (transactionData.pinType == PinType.ONLINE_CVM) {
                isoRequest.set(26, "12") //Max number of Pin characters POS device can accept
                isoRequest.set(52, ByteUtils.hexString2ByteArray(transactionData.pinData))
            }

            isoRequest.set(23, ISOUtil.zeropad(transactionData.cardSequenceNo, 3))
            isoRequest.set(55, transactionData.iccData)
            isoRequest.set(59, ISOUtils.buildField59(transactionData, sessionManager.getTerminalId())) //Echo data which is returned unaltered
            isoRequest.set(123, "511101513344101") //POS data code
            isoRequest.set(128, ISOUtil.hex2byte(SIXTY_FOUR_ZEROS)) //Secondary Message Hash Value

            isoRequest.recalcBitMap()
            val prePack = isoRequest.pack()

            isoRequest.set(
                128,
                Sha256Utils.performSha256Hash(
                    ISOUtil.trim(prePack, prePack.size - 64),
                    ISOUtil.hex2byte(sessionManager.getSessionKey())
                )
            )

            ISOUtils.parseResponse(String(isoRequest.pack()), transactionPackager) //just to print the fields in log

            channel.send(isoRequest)
            isoResponse = channel.receive()
            ISOUtils.parseResponse(String(isoResponse.pack()), transactionPackager)
            channel.disconnect()

            //Confirm if there's a response
            if (isoResponse.hasField("39")) {
                ISOUtils.parseResponse(String(isoResponse.pack()), transactionPackager)

                //set response back to balance response lambda in viewModel
                val transactionResponse = TransactionResponse().apply {
                    responseCode = isoResponse.getString(39)
                    authCode = isoResponse.getString(38)
                    responseMessage = IsoResponseUtils.formatIsoResponse(isoResponse.getString(39))
                    iccData = isoResponse.getString(55)

                    if (isoResponse.getString(39) == "00" && transactionData.transType == TransactionType.BALANCE) {
                        accountBalance = isoResponse.getString(54).substring(9, 20)
                    }
                }
                updateBalanceResponse(transactionResponse)
            }

        } catch (e: ISOException) {
            val transactionResponse = TransactionResponse().apply {
                responseCode = ERROR_PACKAGING_MESSAGE
                responseMessage = "Error packaging message"
            }
            "ISOException error - ".plus(e.message).logThis("Tagg")
            updateBalanceResponse(transactionResponse)
        }
        catch (e: IOException) {
            val transactionResponse = TransactionResponse().apply {
                responseCode = TIME_OUT
                responseMessage = "Request time Out"
            }

            "IOException error - ".plus(e.printStackTrace()).logThis("Tagg")
            updateBalanceResponse(transactionResponse)
        }
         catch (e: EOFException) {
             val transactionResponse = TransactionResponse().apply {
                 responseCode = HOST_DISCONNECT
                 responseMessage = "Host disconnect"
             }
             "EOFException error - ".plus(e.message).logThis("Tagg")
             updateBalanceResponse(transactionResponse)
        }

        return isoResponse
    }
}