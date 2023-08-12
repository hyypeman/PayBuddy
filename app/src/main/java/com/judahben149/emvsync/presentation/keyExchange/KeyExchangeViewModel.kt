package com.judahben149.emvsync.presentation.keyExchange

import android.content.SharedPreferences
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.judahben149.emvsync.domain.SocketClient
import com.judahben149.emvsync.domain.model.NIBSSPackager
import com.judahben149.emvsync.domain.usecase.KeyExchangeUseCase
import com.judahben149.emvsync.utils.*
import com.judahben149.emvsync.utils.Constants.TERMINAL_MASTER_KEY
import com.judahben149.emvsync.utils.Constants.TERMINAL_PIN_KEY
import com.nexgo.oaf.apiv3.DeviceEngine
import com.nexgo.oaf.apiv3.device.pinpad.WorkKeyTypeEnum
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class KeyExchangeViewModel @Inject constructor(
    private val deviceEngine: DeviceEngine,
    private val keyExchangeUseCase: KeyExchangeUseCase,
    private val sharedPreferences: SharedPreferences,
    private val sessionManager: SessionManager
): ViewModel() {

    private var _state: MutableStateFlow<KeyExchangeState> = MutableStateFlow(KeyExchangeState())
    val state: StateFlow<KeyExchangeState> = _state.asStateFlow()


    fun doKeyExchange() {
        val packager = NIBSSPackager()
        val channel = SocketClient.getClient(
            sessionManager.getHostIpAddress(),
            sessionManager.getPortNumber(),
            packager
        )
        "KeyExchangeViewModel - started doExchange()".logThis()

        viewModelScope.launch(Dispatchers.IO) { //do TMK
            val tmkResponse = keyExchangeUseCase.doTMKTransaction(channel)

            if (tmkResponse.isSuccessful()) { // if TMK successful, do TSK
                _state.update { it.copy(isMasterKeyReceived = true) }
                val tskResponse = keyExchangeUseCase.doTSKTransaction(channel)

                if (tskResponse.isSuccessful()) { // if TSK successful, do TPK
                    _state.update { it.copy(isSessionKeyReceived = true) }
                    val tpkResponse = keyExchangeUseCase.doTPKTransaction(channel)

                    //do parameter download also
                    val parameterDownloadResp =
                        keyExchangeUseCase.doParameterDownloadTransaction(channel)

                    if (parameterDownloadResp.isSuccessful()) {
                        _state.update { it.copy(isParameterReceived = true) }
                    } else {
                        onError(parameterDownloadResp.getMessage())
                    }

                    if (tmkResponse.isSuccessful() && tskResponse.isSuccessful() && tpkResponse.isSuccessful()) {
                        _state.update { it.copy(isPinKeyReceived = true) }
                        injectKeys()
                    } else {
                        onError(tpkResponse.getMessage())
                    }
                } else {
                    onError(tskResponse.getMessage())
                }
            } else {
                onError(tmkResponse.getMessage())
            }
        }
    }


    private fun injectKeys() {
        val masterKeyResult = injectMasterKey()

        if (masterKeyResult == 0) {
            _state.update { it.copy(isMasterKeyInjected = true) }
            injectPinKey()
        } else {
            onError("Master key NOT injected!")
        }
    }

    private fun injectMasterKey(): Int {
        val pinPad = deviceEngine.pinPad
        pinPad.deleteMKey(10)

        val key = sharedPreferences.fetchString(TERMINAL_MASTER_KEY)?.let {
            HexUtils.hexStringToByteArray(it)
        }

        return pinPad.writeMKey(5, key, key!!.size)
    }

    private fun injectPinKey() {
        val pinPad = deviceEngine.pinPad

        val key = sharedPreferences.fetchString(TERMINAL_PIN_KEY)?.let {
            HexUtils.hexStringToByteArray(it)
        }

        val result = pinPad.writeWKey(5, WorkKeyTypeEnum.PINKEY, key, key!!.size)
        
        if (result == 0) {
            _state.update { it.copy(isPinKeyInjected = true) }
        } else {
            onError("Pin key NOT injected!")
        }
    }

    private fun onError(message: String) {
        _state.update { it.copy(isFailure = true, errorMessage = message) }
    }
}