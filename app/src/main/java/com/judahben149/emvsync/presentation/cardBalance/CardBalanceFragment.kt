package com.judahben149.emvsync.presentation.cardBalance

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.google.android.material.snackbar.Snackbar
import com.judahben149.emvsync.databinding.FragmentCardBalanceBinding
import com.judahben149.emvsync.domain.model.card.CardType
import com.judahben149.emvsync.domain.model.card.TransactionData
import com.judahben149.emvsync.utils.*
import com.judahben149.emvsync.utils.Constants.TERMINAL_CAPABILITY_CVM
import com.judahben149.emvsync.utils.emvUtils.EmvUtils
import com.judahben149.emvsync.utils.isoUtils.AmountUtils
import com.nexgo.common.ByteUtils
import com.nexgo.oaf.apiv3.DeviceEngine
import com.nexgo.oaf.apiv3.SdkResult
import com.nexgo.oaf.apiv3.device.pinpad.OnPinPadInputListener
import com.nexgo.oaf.apiv3.device.reader.CardInfoEntity
import com.nexgo.oaf.apiv3.device.reader.CardReader
import com.nexgo.oaf.apiv3.device.reader.CardSlotTypeEnum
import com.nexgo.oaf.apiv3.device.reader.OnCardInfoListener
import com.nexgo.oaf.apiv3.emv.*
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class CardBalanceFragment : Fragment(), OnCardInfoListener, OnEmvProcessListener2, OnPinPadInputListener {

    private var _binding: FragmentCardBalanceBinding? = null
    private val binding get() = _binding!!

    @Inject
    lateinit var deviceEngine: DeviceEngine

    @Inject
    lateinit var emvHandler2: EmvHandler2

    @Inject
    lateinit var emvUtils: EmvUtils

    @Inject
    lateinit var sessionManager: SessionManager

    private val viewModel: CardBalanceViewModel by viewModels()

    lateinit var cardReader: CardReader

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentCardBalanceBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        cardReader = deviceEngine.cardReader

        emvUtils.initializeEmvAid(emvHandler2)
        emvUtils.initializeEmvCapk(emvHandler2)

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.state.collect { state ->
                    if (state.isCardInfoReceived) {
                        viewModel.cardInfoState.value.tk2.toString().logThis("Tagg")
                    }
                }
            }
        }

        beginEmv()
    }

    private fun beginEmv() {
        val slotTypes = HashSet<CardSlotTypeEnum>()
        slotTypes.add(CardSlotTypeEnum.ICC1)
        slotTypes.add(CardSlotTypeEnum.ICC2)
        slotTypes.add(CardSlotTypeEnum.RF)

        cardReader.searchCard(slotTypes, 60, this)
    }

    override fun onCardInfo(returnCode: Int, cardInfo: CardInfoEntity?) {
        BeeperUtils.beepOnce(deviceEngine)
        "Card Number - ${ emvHandler2.getEmvCardDataInfo().cardNo }".logThis("Tagg")

        if (returnCode == SdkResult.Success && cardInfo != null) {
            cardReader.stopSearch()
            val emvTransDataEntity = EmvTransConfigurationEntity()

            when(cardInfo.cardExistslot) {
                CardSlotTypeEnum.ICC1 -> {
                    viewModel.updateCardType(CardType.ICC)
                    emvTransDataEntity.emvEntryModeEnum = EmvEntryModeEnum.EMV_ENTRY_MODE_CONTACT

                    emvTransDataEntity.transAmount = AmountUtils.toIsoAmount(
                        viewModel.transactionDataState.value.amount,
                        sessionManager.getCurrencyCode()
                    )

                    emvTransDataEntity.emvTransType = (0x00.toByte())
                    emvTransDataEntity.traceNo = ISOUtils.getStan()
                    emvTransDataEntity.termId = sessionManager.getTerminalId()
                    emvTransDataEntity.merId = sessionManager.getMerchantId()
                    emvTransDataEntity.transDate = DateUtils.transactionDate()
                    emvTransDataEntity.transTime = DateUtils.transactionTime()

                    emvTransDataEntity.currencyCode = sessionManager.getCurrencyCode()
                    emvTransDataEntity.countryCode = sessionManager.getCountryCode()
                    emvTransDataEntity.emvProcessFlowEnum = EmvProcessFlowEnum.EMV_PROCESS_FLOW_STANDARD

                    updateViewModelWithEmvDetails(emvTransDataEntity)

                    emvHandler2.setTlv(ByteArray(0x9F, { 0x33 }), ByteUtils.hexString2ByteArray(TERMINAL_CAPABILITY_CVM))
//                            emvHandler2.initTermConfig()
                    emvHandler2.setTlv(ByteArray(0x9C), HexUtils.hexStringToByteArray("31"))  // Set transaction Type - 31 is for balance enquiry

                    emvHandler2.emvProcess(emvTransDataEntity, this)
                }

                CardSlotTypeEnum.RF -> {  }

                CardSlotTypeEnum.SWIPE -> {  }

                else -> { throw java.lang.IllegalStateException("Unknown Card Type") }
            }
        } else if (returnCode == SdkResult.TimeOut) {

        } else if (returnCode == SdkResult.Fail) {

        }
    }

    override fun onSwipeIncorrect() {

    }

    override fun onMultipleCards() {

    }

    private fun updateViewModelWithEmvDetails(emvTransDataEntity: EmvTransConfigurationEntity) {
        viewModel.updateEmvTransaction(
            stan = emvTransDataEntity.traceNo,
            transTime = emvTransDataEntity.transTime,
            transDate = emvTransDataEntity.transDate
        )
    }


    override fun onSelApp(
        p0: MutableList<String>?,
        p1: MutableList<CandidateAppInfoEntity>?,
        p2: Boolean
    ) {
        emvHandler2.onSetSelAppResponse(2)
    }

    override fun onTransInitBeforeGPO() {

    }

    override fun onConfirmCardNo(cardInfo: CardInfoEntity?) {
        "<<<<<<<<<<onConfirmCardNo>>>>>>>>>>".logThis("Tagg")
        "Card Number** - ${ emvHandler2.emvCardDataInfo.cardNo }".logThis("Tagg")
        "Track 2 data - ${ cardInfo!!.cardNo }".logThis("Tagg")
    }

    override fun onCardHolderInputPin(p0: Boolean, p1: Int) {

    }

    override fun onContactlessTapCardAgain() {

    }

    override fun onOnlineProc() {

    }

    override fun onPrompt(p0: PromptEnum?) {

    }

    override fun onRemoveCard() {

    }

    override fun onFinish(p0: Int, p1: EmvProcessResultEntity?) {

    }

    override fun onInputResult(p0: Int, p1: ByteArray?) {

    }

    override fun onSendKey(p0: Byte) {

    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}