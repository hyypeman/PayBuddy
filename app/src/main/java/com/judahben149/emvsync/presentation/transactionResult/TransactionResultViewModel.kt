package com.judahben149.emvsync.presentation.transactionResult

import androidx.lifecycle.ViewModel
import com.judahben149.emvsync.domain.model.card.TransactionData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class TransactionResultViewModel @Inject constructor(): ViewModel() {

    private var _state: MutableStateFlow<TransactionResultState> = MutableStateFlow(TransactionResultState())
    val state = _state.asStateFlow()

    fun initializeTransactionData(transactionData: TransactionData) {
        _state.update { it.copy(transactionData = transactionData) }

        //Also get print object ready here so once user clicks print, it is ready and print button is shown
        //This will only be done if transaction is printable though
    }

    fun togglePrintable() {
        _state.update { it.copy(isTransactionPrintable = true) }
    }
}
