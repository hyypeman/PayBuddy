package com.judahben149.emvsync.utils.emvUtils

import android.content.Context
import android.graphics.Typeface
import com.judahben149.emvsync.domain.model.card.TransactionData
import com.judahben149.emvsync.domain.model.card.TransactionStatus
import com.judahben149.emvsync.domain.model.card.TransactionType
import com.nexgo.oaf.apiv3.DeviceEngine
import com.nexgo.oaf.apiv3.device.printer.AlignEnum
import com.nexgo.oaf.apiv3.device.printer.GrayLevelEnum
import com.nexgo.oaf.apiv3.device.printer.OnPrintListener
import org.jpos.iso.ISOCurrency
import java.util.*
import javax.inject.Inject

class PrinterUtils {


    fun printTransaction(
        context: Context,
        deviceEngine: DeviceEngine,
        transactionData: TransactionData
    ) {
        val printer = deviceEngine.printer
        printer.initPrinter()

        var amount = ""

        if (transactionData.transactionStatus == TransactionStatus.SUCCESS) {

            when (transactionData.transType) {
                TransactionType.PURCHASE, TransactionType.REVERSAL -> {
                    amount = "Amount: USD ${transactionData.amount}"
                }

                TransactionType.BALANCE -> {
                    amount = "Balance: USD " + String.format(
                        Locale.US,
                        "%,.2f")
                }
            }
        } else {
            amount = "Amount: N/A"
        }

        printer.apply {
            setTypeface(Typeface.DEFAULT)
            setLetterSpacing(5)
            setGray(GrayLevelEnum.LEVEL_2)


            appendPrnStr("***EMV SYNC***", 26, AlignEnum.CENTER, true)
            appendPrnStr(transactionData.transType.name, 24, AlignEnum.CENTER, true)

            appendPrnStr(amount, 24, AlignEnum.LEFT, false)

            appendPrnStr(
                "Card Holder Name:  ${transactionData.cardHolderName}",
                24,
                AlignEnum.LEFT,
                false
            )
            appendPrnStr(
                "Card Number:  ${EmvUtils(context).maskCreditCardNumber(transactionData.cardNo!!)}",
                24,
                AlignEnum.LEFT,
                false
            )
            appendPrnStr("Card Type:  ${transactionData.cardHolderName}", 24, AlignEnum.LEFT, false)
            appendPrnStr(
                "Card Expiry Date:  ${transactionData.expiryDate}",
                24,
                AlignEnum.LEFT,
                false
            )
            appendPrnStr("Card Type:  ${transactionData.appLabel}", 24, AlignEnum.LEFT, false)
            appendPrnStr("Reference Number:  ${transactionData.stan}", 24, AlignEnum.LEFT, false)
            appendPrnStr("", 24, AlignEnum.LEFT, false)
            appendPrnStr("", 24, AlignEnum.LEFT, false)
            appendPrnStr("", 24, AlignEnum.LEFT, false)
            appendPrnStr("", 24, AlignEnum.LEFT, false)

            startPrint(false, OnPrintListener {

            })
        }
    }
}