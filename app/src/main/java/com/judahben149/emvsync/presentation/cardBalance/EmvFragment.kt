package com.judahben149.emvsync.presentation.cardBalance

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.judahben149.emvsync.databinding.FragmentCardBalanceBinding
import com.judahben149.emvsync.domain.model.card.CardType
import com.judahben149.emvsync.domain.model.card.PinType
import com.judahben149.emvsync.domain.model.card.TransactionType
import com.judahben149.emvsync.utils.*
import com.judahben149.emvsync.utils.Constants.IS_PURCHASE_TRANSACTION
import com.judahben149.emvsync.utils.Constants.PURCHASE_AMOUNT
import com.judahben149.emvsync.utils.emvUtils.EmvUtils
import com.judahben149.emvsync.utils.isoUtils.AmountUtils
import com.judahben149.emvsync.utils.isoUtils.ISOUtils
import com.nexgo.common.ByteUtils
import com.nexgo.oaf.apiv3.DeviceEngine
import com.nexgo.oaf.apiv3.SdkResult
import com.nexgo.oaf.apiv3.device.pinpad.*
import com.nexgo.oaf.apiv3.device.reader.CardInfoEntity
import com.nexgo.oaf.apiv3.device.reader.CardReader
import com.nexgo.oaf.apiv3.device.reader.CardSlotTypeEnum
import com.nexgo.oaf.apiv3.device.reader.OnCardInfoListener
import com.nexgo.oaf.apiv3.emv.*
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class EmvFragment : Fragment(), OnCardInfoListener, OnEmvProcessListener2,
    OnPinPadInputListener {

    private val navController by lazy {
        findNavController()
    }

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

    private val viewModel: EmvViewModel by viewModels()

    lateinit var cardReader: CardReader
    lateinit var pinPad: PinPad

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
        pinPad = deviceEngine.pinPad

        val isPurchaseTransaction = arguments?.getBoolean(IS_PURCHASE_TRANSACTION)
        val amount = arguments?.getDouble(PURCHASE_AMOUNT)

        isPurchaseTransaction?.let {  isPurchaseTransaction ->
            if (isPurchaseTransaction) {
                amount?.let { amount ->
                    viewModel.updateTransactionType(amount, TransactionType.PURCHASE)
                }
            }
        }

        observeState()
        setClickListeners()
        beginEmvProcess()
    }

    private fun beginEmvProcess() {
        val slotTypes = HashSet<CardSlotTypeEnum>()
        slotTypes.add(CardSlotTypeEnum.ICC1)
        slotTypes.add(CardSlotTypeEnum.RF)

        cardReader.searchCard(slotTypes, 60, this)
    }

    override fun onCardInfo(returnCode: Int, cardInfo: CardInfoEntity?) {
        BeeperUtils.beepOnce(deviceEngine)

        if (returnCode == SdkResult.Success && cardInfo != null) {
            cardReader.stopSearch()

            val emvTransDataEntity = EmvTransConfigurationEntity()

            emvTransDataEntity.transAmount = AmountUtils.toIsoAmount(
                viewModel.transactionDataState.value.amount,
                sessionManager.getCurrencyCode()
            )

            emvTransDataEntity.termId = sessionManager.getTerminalId()
            emvTransDataEntity.emvTransType = (0x00.toByte())
            emvTransDataEntity.merId = sessionManager.getMerchantId()
            emvTransDataEntity.transDate = DateUtils.transactionDate()
            emvTransDataEntity.transTime = DateUtils.transactionTime()
            emvTransDataEntity.traceNo = ISOUtils.getStan()

            emvTransDataEntity.emvProcessFlowEnum = EmvProcessFlowEnum.EMV_PROCESS_FLOW_STANDARD
            emvTransDataEntity.countryCode = sessionManager.getCountryCode()
            emvTransDataEntity.currencyCode = sessionManager.getCurrencyCode()

            updateViewModelWithEmvDetails(emvTransDataEntity)

            when (cardInfo.cardExistslot) {
                CardSlotTypeEnum.ICC1 -> {
                    viewModel.updateCardType(CardType.ICC)
                    emvTransDataEntity.emvEntryModeEnum = EmvEntryModeEnum.EMV_ENTRY_MODE_CONTACT
                }

                CardSlotTypeEnum.RF -> {
                    viewModel.updateCardType(CardType.RF)
                    emvTransDataEntity.emvEntryModeEnum = EmvEntryModeEnum.EMV_ENTRY_MODE_CONTACTLESS
                }

                CardSlotTypeEnum.SWIPE -> {}

                else -> {
                    throw java.lang.IllegalStateException("Unknown Card Type")
                }
            }

            emvHandler2.emvProcess(emvTransDataEntity, this)

        } else if (returnCode == SdkResult.TimeOut) {

        } else if (returnCode == SdkResult.Fail) {

        }
    }

    override fun onSwipeIncorrect() {

    }

    override fun onMultipleCards() {

    }

    override fun onContactlessTapCardAgain() {

    }

    override fun onSelApp(
        appNameList: MutableList<String>?,
        appInfoList: MutableList<CandidateAppInfoEntity>?,
        isFirstSelect: Boolean
    ) {
        emvHandler2.onSetSelAppResponse(1 + 1)
    }

    override fun onTransInitBeforeGPO() {
        val aid = emvHandler2.getTlv(byteArrayOf(0x4F), EmvDataSourceEnum.FROM_KERNEL)
        emvHandler2.onSetTransInitBeforeGPOResponse(true)
    }

    override fun onConfirmCardNo(cardInfo: CardInfoEntity?) {
        "<<<<<<<<<<onConfirmCardNo>>>>>>>>>>".logThis("Tagg")

        cardInfo?.let {
            viewModel.updateCardInfo(cardInfo)
        }

        val cardNumber = emvHandler2.emvCardDataInfo.cardNo

        lifecycleScope.launch(Dispatchers.Main) {
            showConfirmationDialog(
                childFragmentManager = childFragmentManager,
                title = "Confirm Card Number",
                body = "Please confirm card number - ${emvUtils.maskCreditCardNumber(cardNumber)}",
                actionPositive = {
                    BeeperUtils.beepOnce(deviceEngine)
                    emvHandler2.onSetConfirmCardNoResponse(true)
                },
                actionNegative = {
                    BeeperUtils.beepOnce(deviceEngine)
                    emvHandler2.onSetConfirmCardNoResponse(false)
                    emvHandler2.emvProcessCancel()
                }
            )
        }
    }

    override fun onCardHolderInputPin(isOnlinePin: Boolean, leftTimes: Int) {
        "<<<<<<<<<<onCardHolderInputPin>>>>>>>>>>".logThis("Tagg")
        pullUpKeyPad(isOnlinePin)
    }

    private fun pullUpKeyPad(isOnlinePin: Boolean) {
        pinPad.setPinKeyboardMode(PinKeyboardModeEnum.FIXED)

        if (isOnlinePin) {
            viewModel.updatePinType(PinType.ONLINE_CVM)
            pinPad.inputOnlinePin( // first parameter is an array of acceptable PIN lengths namely 0 (PIN bypass), and 4 up till 12
                intArrayOf(0x00, 0x04, 0x05, 0x06, 0x07, 0x08, 0x09, 0x0a, 0x0b, 0x0c),
                60,
                emvHandler2.emvCardDataInfo.cardNo.toByteArray(),
                5,
                PinAlgorithmModeEnum.ISO9564FMT1,
                this
            )
        } else {
            viewModel.updatePinType(PinType.OFFLINE_CVM)
            pinPad.inputOfflinePin(
                intArrayOf(0x00, 0x04, 0x05, 0x06, 0x07, 0x08, 0x09, 0x0a, 0x0b, 0x0c),
                60,
                this
            )
        }

        viewModel.shouldShowPinView(true)
    }

    override fun onSendKey(keyCode: Byte) {
        "On Send key fxn entered".logThis("Tagg")

        when(keyCode) {
            PinPadKeyCode.KEYCODE_CLEAR -> viewModel.updatePinLength(true)
            PinPadKeyCode.KEYCODE_STAR -> viewModel.updatePinLength()
        }
    }

    override fun onPrompt(promptEnum: PromptEnum?) {
        "<<<<<<<<<<onPrompt>>>>>>>>>>".logThis("Tagg")

        when (promptEnum) {
            PromptEnum.APP_SELECTION_IS_NOT_ACCEPTED -> "NO APPLICATION SELECTED".logThis("Tagg")
            PromptEnum.OFFLINE_PIN_CORRECT -> {
                "PIN Accepted".logThis("Tagg")
                viewModel.updatePinState(PinEntryState.CORRECT)
            }
            PromptEnum.OFFLINE_PIN_INCORRECT -> {
                "Invalid PIN".logThis("Tagg")
                viewModel.updatePinState(PinEntryState.INCORRECT)
                viewModel.clearPinEntries()
            }
            PromptEnum.OFFLINE_PIN_INCORRECT_TRY_AGAIN -> {
                "Invalid PIN, TRY AGAIN".logThis("Tagg")
                viewModel.updatePinState(PinEntryState.INCORRECT)
                viewModel.clearPinEntries()
            }
            else -> "Error - else branch running".logThis("Tagg")
        }

        emvHandler2.onSetPromptResponse(true)
    }

    override fun onInputResult(retCode: Int, data: ByteArray?) {
        "<<<<<<<<<<On Input Result>>>>>>>>>>".logThis("Tagg")

        when(retCode) {
            SdkResult.Success, SdkResult.PinPad_No_Pin_Input, SdkResult.PinPad_Input_Cancel -> {
                if (data != null) {
                    val temp = ByteArray(8)
                    System.arraycopy(data, 0, temp, 0, 8)
                    viewModel.updatePinData(ByteUtils.byteArray2HexString(data))

                    "Pin Data - ${ ByteUtils.byteArray2HexString(data) }".logThis("Tagg")
                }

                //(var1 = whether valid input / success, var2 = pin bypass)
                emvHandler2.onSetPinInputResponse(retCode != SdkResult.PinPad_Input_Cancel, retCode == SdkResult.PinPad_No_Pin_Input)
            }

            else -> { //PIN entering failed with some other error - Trigger some UI to feedback to user
                emvHandler2.onSetPinInputResponse(false, false)
            }
        }
    }

    override fun onOnlineProc() {
        "<<<<<<<<<<onOnlineProc>>>>>>>>>>".logThis("Tagg")

        val emvOnlineResult = EmvOnlineResultEntity()

        if (emvHandler2.emvCvmResult == null) {
            //End the entire EMV flow and provide feedback to user
        } else {
            viewModel.processTransactionOnline(emvHandler2, requireContext())
        }

        emvOnlineResult.authCode = viewModel.transactionDataState.value.authCode
        emvOnlineResult.rejCode = viewModel.transactionDataState.value.responseCode
        emvOnlineResult.recvField55 = viewModel.transactionDataState.value.iccData?.toByteArray()
        emvHandler2.onSetOnlineProcResponse(SdkResult.Success, emvOnlineResult)
    }



    override fun onRemoveCard() {
        //Alert user to remove card
        emvHandler2.onSetRemoveCardResponse()
    }

    override fun onFinish(retCode: Int, resultEntity: EmvProcessResultEntity?) {
        "On Finish just called".logThis("Tagg")

        lifecycleScope.launch(Dispatchers.Main) {
            emvHandler2.emvProcessAbort()

            when(retCode) {
                SdkResult.Success, SdkResult.Emv_Declined, SdkResult.Emv_Success_Arpc_Fail -> {

                }
                SdkResult.Emv_Card_Block -> {
                    //Alert customer that card is blocked
                }
                SdkResult.Emv_Qpboc_Online -> {
                    pullUpKeyPad(true)
                }
                SdkResult.Emv_Cancel, SdkResult.Emv_Communicate_Timeout -> {
                    //End emv activity and close fragment
                }
                SdkResult.Emv_FallBack -> {
                    //Red card Error - Give user feedback
                }
            }
        }
    }



    private fun updateViewModelWithEmvDetails(emvTransDataEntity: EmvTransConfigurationEntity) {
        viewModel.updateEmvTransaction(
            stan = emvTransDataEntity.traceNo,
            transTime = emvTransDataEntity.transTime,
            transDate = emvTransDataEntity.transDate
        )
    }

    private fun observeState() {

        viewModel.state.flowWithLifecycle(viewLifecycleOwner.lifecycle).onEach { state ->

            if (state.isCardInfoReceived) {
                viewModel.cardInfoState.value.tk2.toString().logThis("Tagg")
            }

            if (state.isReadyForPinInput) {
                binding.animInsertCard.visibility = View.GONE
                binding.btnCancel.visibility = View.GONE
                binding.tvEnterCard.visibility = View.GONE

                binding.pinView.visibility = View.VISIBLE
                binding.tvPinType.visibility = View.VISIBLE
                binding.tvPinStatus.visibility = View.INVISIBLE

                if (viewModel.transactionDataState.value.pinType == PinType.OFFLINE_CVM)
                    binding.tvPinType.text = "Enter OFFLINE PIN"
            }

            when (state.pinEntryState) {
                PinEntryState.INCORRECT ->{
                    binding.tvPinStatus.visibility = View.VISIBLE
                }
                else -> {
                    binding.tvPinStatus.visibility = View.INVISIBLE
                }
            }

            if (state.isProcessingOnline) {
                binding.pinView.visibility = View.GONE
                binding.tvPinType.visibility = View.GONE

                val dialogText = when(viewModel.transactionDataState.value.transType) {
                    TransactionType.BALANCE -> "Retrieving balance..."
                    TransactionType.PURCHASE -> "Initiating transaction..."
                    TransactionType.REVERSAL -> "Processing reversal..."
                }

                hideProgressDialog(childFragmentManager)
                showProgressDialog(childFragmentManager, dialogText)
            } else {
                hideProgressDialog(childFragmentManager)
            }

            if (state.isTransactionCompleted && !state.shouldReverseTransaction) {
                val transactionData = viewModel.transactionDataState.value
                val action = CardBalanceFragmentDirections.actionCardBalanceFragmentToTransactionResultFragment(transactionData)

                navController.navigate(action)
            }

            if (state.shouldReverseTransaction) {
                viewModel.processReversal()
            }

            if (state.isReversalComplete) {
                val transactionData = viewModel.transactionDataState.value
                val action = CardBalanceFragmentDirections.actionCardBalanceFragmentToTransactionResultFragment(transactionData)

                navController.navigate(action)
            }

        }.launchIn(lifecycleScope)

        viewModel.transactionDataState.flowWithLifecycle(viewLifecycleOwner.lifecycle).onEach { transactionState ->
            when (transactionState.pinEnteredLength) {
                0 -> binding.pinView.setPinLength(0)
                1 -> binding.pinView.setPinLength(1)
                2 -> binding.pinView.setPinLength(2)
                3 -> binding.pinView.setPinLength(3)
                4 -> binding.pinView.setPinLength(4)
            }
        }.launchIn(lifecycleScope)
    }

    private fun setClickListeners() {
        binding.btnCancel.setOnClickListener {
            BeeperUtils.beepOnce(deviceEngine)
            emvHandler2.onSetConfirmCardNoResponse(false)
            emvHandler2.emvProcessCancel()
            navController.popBackStack()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        emvHandler2.emvProcessAbort()
        _binding = null
    }
}