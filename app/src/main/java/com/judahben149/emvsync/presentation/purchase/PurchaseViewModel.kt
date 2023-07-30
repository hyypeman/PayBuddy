package com.judahben149.emvsync.presentation.purchase

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class PurchaseViewModel @Inject constructor(): ViewModel() {

    private var _state: MutableStateFlow<PurchaseState> = MutableStateFlow(PurchaseState())
    val state = _state.asStateFlow()

    fun addNumber(number: String) {
        _state.update { it.copy(amount = _state.value.amount + number) }
    }

    fun clear() {
        _state.update { it.copy(amount = "") }
    }

    fun cancel() {
        _state.update { it.copy(amount = _state.value.amount.dropLast(1)) }
    }
}