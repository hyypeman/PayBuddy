package com.judahben149.emvsync.presentation.cardBalance

data class EmvState(
    val isCardInfoReceived: Boolean = false,
    val isReadyForPinInput: Boolean = false,
    val pinEntryState: PinEntryState = PinEntryState.IDLE,
    val isInitializationFromKernelComplete: Boolean = false,
    val isProcessingOnline: Boolean = false,
    val isTransactionCompleted: Boolean = false,
    val shouldReverseTransaction: Boolean = false,
    val isReversalComplete: Boolean = false
)

enum class PinEntryState {
    CORRECT,
    INCORRECT,
    IDLE,
}
