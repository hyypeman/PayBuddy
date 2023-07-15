package com.judahben149.emvsync.presentation.cardBalance

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.judahben149.emvsync.domain.mapper.toCardInfo
import com.judahben149.emvsync.domain.model.card.CardInfo
import com.judahben149.emvsync.domain.model.card.CardType
import com.judahben149.emvsync.domain.model.card.TransactionData
import com.nexgo.oaf.apiv3.device.reader.CardInfoEntity
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class CardBalanceViewModel @Inject constructor(): ViewModel() {

    private var _state: MutableStateFlow<CardBalanceState> = MutableStateFlow(CardBalanceState())
    val state: StateFlow<CardBalanceState> = _state.asStateFlow()

    private var _cardInfoState: MutableStateFlow<CardInfo> = MutableStateFlow(CardInfo())
    val cardInfoState: StateFlow<CardInfo> = _cardInfoState.asStateFlow()

    private var _transactionDataState: MutableStateFlow<TransactionData> = MutableStateFlow(TransactionData())
    val transactionDataState: StateFlow<TransactionData> = _transactionDataState.asStateFlow()


    fun updateCardInfo(cardInfoEntity: CardInfoEntity) {
        _cardInfoState.update { cardInfoEntity.toCardInfo() }
        _state.update { it.copy(isCardInfoReceived = true) }
    }

    fun updateCardType(cardType: CardType) {
        if (cardType == CardType.ICC)
            _cardInfoState.update { it.copy(isICC = true) }
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
}



