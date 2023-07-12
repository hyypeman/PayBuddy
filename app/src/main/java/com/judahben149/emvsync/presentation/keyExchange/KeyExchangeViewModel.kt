package com.judahben149.emvsync.presentation.keyExchange

import android.content.SharedPreferences
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.judahben149.emvsync.domain.SocketClient
import com.judahben149.emvsync.domain.model.NIBSSPackager
import com.judahben149.emvsync.iso.KeyExchangeHandler
import com.judahben149.emvsync.utils.Constants
import com.judahben149.emvsync.utils.Constants.IP_ADDRESS
import com.judahben149.emvsync.utils.Constants.PORT
import com.judahben149.emvsync.utils.Constants.TERMINAL_MASTER_KEY
import com.judahben149.emvsync.utils.Constants.TERMINAL_PIN_KEY
import com.judahben149.emvsync.utils.HexUtils
import com.judahben149.emvsync.utils.fetchString
import com.judahben149.emvsync.utils.logThis
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
    private val keyExchangeHandler: KeyExchangeHandler,
    private val sharedPreferences: SharedPreferences
): ViewModel() {

    private var _state: MutableStateFlow<KeyExchangeState> = MutableStateFlow(KeyExchangeState())
    val state: StateFlow<KeyExchangeState> = _state.asStateFlow()


    fun doKeyExchange() {
        val packager = NIBSSPackager()
        "KeyExchangeViewModel - started doExchange()".logThis()

        viewModelScope.launch(Dispatchers.IO) { //do TMK
            val tmkResponse = keyExchangeHandler.doTMKTransaction(SocketClient.getClient(IP_ADDRESS, PORT, packager))

            if (tmkResponse.isSuccessful()) { // if TMK successful, do TSK
                _state.update { it.copy(isMasterKeyReceived = true) }
                val tskResponse = keyExchangeHandler.doTSKTransaction(SocketClient.getClient(IP_ADDRESS, PORT, packager))

                if (tskResponse.isSuccessful()) { // if TSK successful, do TPK
                    _state.update { it.copy(isSessionKeyReceived = true) }
                    val tpkResponse = keyExchangeHandler.doTPKTransaction(SocketClient.getClient(IP_ADDRESS, PORT, packager))

                    //do parameter download also
                    val parameterDownloadResp =
                        keyExchangeHandler.doParameterDownloadTransaction(SocketClient.getClient(IP_ADDRESS, PORT, packager))

                    if (parameterDownloadResp.isSuccessful()) 
                        _state.update { it.copy(isParameterReceived = true) }
                    
                    if (tmkResponse.isSuccessful() && tskResponse.isSuccessful() && tpkResponse.isSuccessful()) {
                        _state.update { it.copy(isPinKeyReceived = true) }
                        injectKeys()
                    }
                } else {
                    tskResponse.getMessage()
                }
            } else {
                tmkResponse.getMessage()
            }
        }
    }


    private fun injectKeys() {
        val masterKeyResult = injectMasterKey()

        if (masterKeyResult == 0) {
            _state.update { it.copy(isMasterKeyInjected = true) }
            injectPinKey()
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
        
        if (result == 0)
            _state.update { it.copy(isPinKeyInjected = true) }
    }
}