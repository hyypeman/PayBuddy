package com.judahben149.emvsync.presentation.transactionResult

import com.judahben149.emvsync.domain.model.card.TransactionData

data class TransactionResultState(
    val transactionData: TransactionData? = null,
    val isTransactionPrintable: Boolean = false,
    val isReadyToPrint: Boolean = false,
    val isPrintingComplete: Boolean = false,
)