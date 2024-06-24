package com.judahben149.emvsync.presentation.settings

import androidx.lifecycle.ViewModel
import com.judahben149.emvsync.utils.SessionManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(private val sessionManager: SessionManager) : ViewModel() {

    private var _state: MutableStateFlow<SettingsState> = MutableStateFlow(SettingsState())
    val state = _state.asStateFlow()

    init {
        getParameters()
    }

    private fun getParameters() {
        _state.update { it.copy(
            terminalNumber = sessionManager.getTerminalId(),
            acquiringInstitutionCode = sessionManager.getAcquiringInstitutionCode()
        ) }
    }

    fun updateParameters(terminalNo: String, acquirerId: String) {
        sessionManager.apply {
            saveTerminalNumber(terminalNo)
            saveAcquirerID(acquirerId)
        }
    }
}