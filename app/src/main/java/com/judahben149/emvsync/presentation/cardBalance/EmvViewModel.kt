package com.judahben149.emvsync.presentation.cardBalance

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.judahben149.emvsync.BuildConfig
import com.judahben149.emvsync.domain.SocketClient
import com.judahben149.emvsync.domain.mapper.toCardInfo
import com.judahben149.emvsync.domain.model.NIBSSPackager
import com.judahben149.emvsync.domain.model.card.*
import com.judahben149.emvsync.domain.usecase.TransactionUseCase
import com.judahben149.emvsync.utils.Constants.SUCCESSFUL
import com.judahben149.emvsync.utils.HexUtils
import com.judahben149.emvsync.utils.SessionManager
import com.judahben149.emvsync.utils.constants.EmvConstants
import com.judahben149.emvsync.utils.isoUtils.ISOUtils
import com.judahben149.emvsync.utils.constants.PosEntryMode
import com.judahben149.emvsync.utils.emvUtils.EmvUtils
import com.judahben149.emvsync.utils.logThis
import com.nexgo.common.ByteUtils
import com.nexgo.oaf.apiv3.device.reader.CardInfoEntity
import com.nexgo.oaf.apiv3.device.reader.CardSlotTypeEnum
import com.nexgo.oaf.apiv3.emv.EmvDataSourceEnum
import com.nexgo.oaf.apiv3.emv.EmvHandler2
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.jpos.iso.ISOUtil
import javax.inject.Inject


@HiltViewModel
class EmvViewModel @Inject constructor(
    private val transactionUseCase: TransactionUseCase,
    private val sessionManager: SessionManager
): ViewModel() {

    private var _emvState: MutableStateFlow<EmvState> = MutableStateFlow(EmvState())
    val state: StateFlow<EmvState> = _emvState.asStateFlow()

    private var _cardInfoState: MutableStateFlow<CardInfo> = MutableStateFlow(CardInfo())
    val cardInfoState: StateFlow<CardInfo> = _cardInfoState.asStateFlow()

    private var _transactionDataState: MutableStateFlow<TransactionData> = MutableStateFlow(TransactionData())
    val transactionDataState: StateFlow<TransactionData> = _transactionDataState.asStateFlow()


    fun updateCardInfo(cardInfoEntity: CardInfoEntity) {
        _cardInfoState.update { cardInfoEntity.toCardInfo() }
        _emvState.update { it.copy(isCardInfoReceived = true) }
    }

    fun updateCardType(cardType: CardType) {
        if (cardType == CardType.ICC)
            _cardInfoState.update { it.copy(isICC = true) }
    }

    fun updateTransactionType(amount: Double, type: TransactionType) {
        _transactionDataState.update { it.copy(transType = type, amount = amount) }
    }

    fun updateEmvTransaction(
        stan: String,
        transTime: String,
        transDate: String,
    ) {
        _transactionDataState.update { it.copy(
            stan = stan,
            transTime = transTime,
            transDate = transDate
        ) }
    }

    fun shouldShowPinView(value: Boolean) {
        _emvState.update { it.copy(isReadyForPinInput = value) }
    }

    fun updatePinType(pinType: PinType) {
        _transactionDataState.update { it.copy(pinType = pinType) }
    }

    fun updatePinState(state: PinEntryState) {
        _emvState.update { it.copy(pinEntryState = state) }
    }

    fun updatePinLength(shouldClear: Boolean = false) {
        _transactionDataState.update { it.copy(pinEnteredLength = _transactionDataState.value.pinEnteredLength + 1) }
        updatePinState(PinEntryState.IDLE)
    }

    fun clearPinEntries() {
        _transactionDataState.update { it.copy(pinEnteredLength = 0) }
    }

    fun updatePinData(pinData: String) {
        _transactionDataState.update { it.copy(pinData = pinData) }
    }

    fun processTransactionOnline(emvHandler: EmvHandler2, context: Context) {
        _emvState.update { it.copy(isProcessingOnline = true) }

        viewModelScope.launch(Dispatchers.IO) {
            val isInitializationComplete = initializeExtraTerminalDetailsFromKernel(emvHandler, context)

            if (isInitializationComplete) {
                val packager = NIBSSPackager()
                val channel = SocketClient.getClient(sessionManager.getHostURL(), packager)

                val isoResponse = transactionUseCase.buildISOMsg(channel, _transactionDataState.value) { transactionResponse ->
                    transactionResponse.toString().logThis("Taggg")


                    if (transactionResponse.responseCode == SUCCESSFUL) {
                        if (_transactionDataState.value.transType == TransactionType.BALANCE) {
                            _transactionDataState.update { it.copy(accountBalance = transactionResponse.accountBalance) }
                        }

                        _transactionDataState.update {
                            it.copy(
                                transactionStatus = TransactionStatus.SUCCESS,
                                responseCode = transactionResponse.responseCode,
                                responseMessage = transactionResponse.responseMessage,
                                authCode = transactionResponse.authCode,
                                iccData = transactionResponse.iccData,
                            )
                        }
                        _emvState.update { it.copy(isTransactionCompleted = true, isProcessingOnline = false) }
                    }
                    else if (_transactionDataState.value.transType != TransactionType.BALANCE && EmvConstants.REVERSAL_CODES.contains(transactionResponse.responseCode)) { //Process reversal
                        _transactionDataState.update { it.copy(transType = TransactionType.REVERSAL) }
                        _emvState.update { it.copy(isTransactionCompleted = true, isProcessingOnline = false, shouldReverseTransaction = true) }


                    }
                    else {
                        _transactionDataState.update {
                            it.copy(
                                transactionStatus = TransactionStatus.FAILURE,
                                responseCode = transactionResponse.responseCode,
                                responseMessage = transactionResponse.responseMessage,
                                authCode = transactionResponse.authCode,
                                iccData = transactionResponse.iccData,
                            )
                        }
                        _emvState.update { it.copy(isTransactionCompleted = true, isProcessingOnline = false) }
                    }
                }
            }
        }
    }

    fun processReversal() {
        _emvState.update { it.copy(isProcessingOnline = true) }

        viewModelScope.launch(Dispatchers.IO) {
            val packager = NIBSSPackager()
            val channel = SocketClient.getClient(
                sessionManager.getHostURL(),
                packager
            )

            val isoResponse = transactionUseCase.buildISOMsg(channel, _transactionDataState.value) { transactionResponse ->
                transactionResponse.toString().logThis("Taggg")

                if (transactionResponse.responseCode == SUCCESSFUL) {

                    _transactionDataState.update {
                        it.copy(
                            transactionStatus = TransactionStatus.SUCCESS,
                            responseCode = transactionResponse.responseCode,
                            responseMessage = transactionResponse.responseMessage,
                            authCode = transactionResponse.authCode,
                            iccData = transactionResponse.iccData,
                            isReversal = true
                        )
                    }
                    _emvState.update { it.copy(isReversalComplete = true, isProcessingOnline = false) }
                } else {
                    _transactionDataState.update {
                        it.copy(
                            transactionStatus = TransactionStatus.FAILURE,
                            responseCode = transactionResponse.responseCode,
                            responseMessage = transactionResponse.responseMessage,
                            authCode = transactionResponse.authCode,
                            iccData = transactionResponse.iccData,
                            isReversal = true
                        )
                    }
                    _emvState.update { it.copy(isTransactionCompleted = true, shouldReverseTransaction = false, isProcessingOnline = false) }
                }
            }
        }
    }

    private fun initializeExtraTerminalDetailsFromKernel(emvHandler: EmvHandler2, context: Context): Boolean {
        val cardInfo = emvHandler.emvCardDataInfo

        _transactionDataState.update {
            it.copy(
                cardNo = cardInfo.cardNo,
                expiryDate = cardInfo.expiredDate,
                cardSequenceNo = cardInfo.csn,
                serviceCode = cardInfo.serviceCode,
                track2Data = cardInfo.tk2
            )
        }


        if (cardInfo.cardExistslot == CardSlotTypeEnum.RF) {
            //Pan entry mode
            emvHandler.setTlv(byteArrayOf(0x9F.toByte(), 0x39.toByte()), ByteUtils.hexString2ByteArray(PosEntryMode.CONTACTLESS_MAGNETIC_STRIPE))
            _transactionDataState.update { it.copy(contactlessMode = ContactlessModeConstant.MSD) }
        } else {
            emvHandler.setTlv(byteArrayOf(0x9F.toByte(), 0x39.toByte()), ByteUtils.hexString2ByteArray(PosEntryMode.CONTACTLESS_INTEGRATED_CIRCUIT_CARD))
            _transactionDataState.update { it.copy(contactlessMode = ContactlessModeConstant.EMV) }
        }

        //initialize stan
        emvHandler.setTlv(byteArrayOf(0x9F.toByte(), 0x41.toByte()), _transactionDataState.value.stan?.let { HexUtils.hexStringToByteArray(it) })

        //get and set ICC data
        val iccTags = arrayOf("5f34", "9f26", "9f27", "9f10", "9f37", "9f36", "95", "9a", "9c",
            "9f02", "5f2a", "82", "9f1a", "9f03", "9f33", "9f34", "9f35", "9f1e", "9f09", "84",
            "9f41", "9f06", "9f6e", "71", "72")
        val iccData = emvHandler.getTlvByTags(iccTags)
        _transactionDataState.update { it.copy(iccData = iccData) }

        //get and set AID
        val aid = ByteUtils.byteArray2HexString(
            emvHandler.getTlv(
                byteArrayOf(0x9F.toByte(), 0x06.toByte()),
                EmvDataSourceEnum.FROM_KERNEL
            )
        )
        _transactionDataState.update { it.copy(aid = aid) }
        "AID - $aid".logThis("Tagg")
        
        
        //get and view PIN key
        val pinKey = ByteUtils.byteArray2HexString(
            emvHandler.getTlv(
                byteArrayOf(0xdf.toByte(), 0x36.toByte()),
                EmvDataSourceEnum.FROM_KERNEL
            )
        )
        //Just print this out to log to view
        "Pin Key - $pinKey".logThis("Tagg")

        //get and view Merchant category code
        val merchantCategoryCode = ByteUtils.byteArray2HexString(
            emvHandler.getTlv(
                byteArrayOf(0x9F.toByte(), 0x15.toByte()),
                EmvDataSourceEnum.FROM_KERNEL
            )
        )
        //Print to log to view
        "MCC - $merchantCategoryCode".logThis("Tagg")

        //get and set Card holder name
        val cardHolderName = ByteUtils.byteArray2HexString(
            emvHandler.getTlv(
                byteArrayOf(0x5F.toByte(), 0x20.toByte()),
                EmvDataSourceEnum.FROM_KERNEL
            )
        )
        _transactionDataState.update { it.copy(cardHolderName = HexUtils.hexToAscii(cardHolderName)) }
        "Card Holder Name - ${ HexUtils.hexToAscii(cardHolderName) }".logThis("Tagg")

        //get and view Extended Card holder name
        val extendedCardHolderName = ByteUtils.byteArray2HexString(
            emvHandler.getTlv(
                byteArrayOf(0x9F.toByte(), 0x0B.toByte()),
                EmvDataSourceEnum.FROM_KERNEL
            )
        )
        //Print to log to view
        "Extended Card Holder Name - ${ HexUtils.hexToAscii(extendedCardHolderName) }".logThis("Tagg")

        //get and set POS Entry Mode
        val posEntryMode = ByteUtils.byteArray2HexString(
            emvHandler.getTlv(
                byteArrayOf(0x9F.toByte(), 0x39.toByte()),
                EmvDataSourceEnum.FROM_KERNEL
            )
        )
        _transactionDataState.update { it.copy(posEntryMode = posEntryMode) }
        "POS Entry Mode - $posEntryMode".logThis("Tagg")


        //get application name which is used later on
        val appPreferredName = ByteUtils.byteArray2HexString(
            emvHandler.getTlv(
                byteArrayOf(0x9F.toByte(), 0x12.toByte()),
                EmvDataSourceEnum.FROM_KERNEL
            )
        )
        "App Preferred Name - ${ HexUtils.hexToAscii(appPreferredName) }".logThis("Tagg")

        //get application label which is used later on
        val appLabel = ByteUtils.byteArray2HexString(
            emvHandler.getTlv(
                byteArrayOf(0x50.toByte()),
                EmvDataSourceEnum.FROM_KERNEL
            )
        )
        "App Label - ${ HexUtils.hexToAscii(appLabel) }".logThis("Tagg")

        //get Issuer Code Table Index which is used later on
        val issuerCodeTableIndex = ByteUtils.byteArray2HexStringNotAppendZero(
            emvHandler.getTlv(
                byteArrayOf(0x9F.toByte(), 0x11.toByte()),
                EmvDataSourceEnum.FROM_KERNEL
            )
        )

        //get and set Application Label Result
        val appLabelResult: String? = EmvUtils(context).getAppLabel(issuerCodeTableIndex, appLabel, appPreferredName)
        _transactionDataState.update { it.copy(appLabel = appLabelResult?.let { appLabel -> HexUtils.hexToAscii(appLabel) }) }
        //print this in log too
        "App Label Result - ${ HexUtils.hexToAscii(appLabelResult!!) }".logThis("Tagg")

        //get and set Transaction Status Information
        val tsi = ByteUtils.byteArray2HexString(
            emvHandler.getTlv(
                ISOUtil.hex2byte("9B"),
                EmvDataSourceEnum.FROM_KERNEL
            )
        )
        _transactionDataState.update { it.copy(tsi = tsi) }

        //get and set Terminal Verification Results
        val tvr = ByteUtils.byteArray2HexString(
            emvHandler.getTlv(
                ISOUtil.hex2byte("95"),
                EmvDataSourceEnum.FROM_KERNEL
            )
        )
        _transactionDataState.update { it.copy(tvr = tvr) }


        //set other details
        val transDateTime = ISOUtils.getIsoTransDateTime()
        val stan = ISOUtils.getStan().toString()

        _transactionDataState.update {
            it.copy(
                transDate = ISOUtils.getIsoTransDate(),
                transTime = ISOUtils.getIsoTransTime(),
                datetime = transDateTime,
                rrn = ISOUtils.generateRetrievalReferenceNumber(transDateTime, stan),
                stan = stan
            )
        }

        _emvState.update { it.copy(isInitializationFromKernelComplete = true) }
        return _emvState.value.isInitializationFromKernelComplete
    }
}



