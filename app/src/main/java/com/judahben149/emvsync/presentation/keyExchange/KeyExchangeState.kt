package com.judahben149.emvsync.presentation.keyExchange

data class KeyExchangeState(
    val isMasterKeyReceived: Boolean = false,
    val isSessionKeyReceived: Boolean = false,
    val isPinKeyReceived: Boolean = false,
    val isParameterReceived: Boolean = false,
    val isMasterKeyInjected: Boolean = false,
    val isPinKeyInjected: Boolean = false,
)