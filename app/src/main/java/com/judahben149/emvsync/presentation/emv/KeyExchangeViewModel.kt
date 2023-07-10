package com.judahben149.emvsync.presentation.emv

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.judahben149.emvsync.domain.SocketClient
import com.judahben149.emvsync.domain.model.NIBSSPackager
import com.judahben149.emvsync.iso.KeyExchangeHandler
import com.judahben149.emvsync.utils.Constants
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class KeyExchangeViewModel @Inject constructor(private val keyExchangeHandler: KeyExchangeHandler): ViewModel() {

    fun doKeyExchange() {
        val packager = NIBSSPackager()

        viewModelScope.launch(Dispatchers.IO) {
            keyExchangeHandler.doTMKTransaction(SocketClient.getClient(Constants.IP_ADDRESS, Constants.PORT, packager))
        }
    }
}