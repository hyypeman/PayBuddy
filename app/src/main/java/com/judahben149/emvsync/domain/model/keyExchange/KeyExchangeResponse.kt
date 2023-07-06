package com.judahben149.emvsync.domain.model.keyExchange

data class KeyExchangeResponse(
    val tmk: String,
    val tsk: String,
    val tpk: String,
    val downloadParameter: DownloadParameter,
)

data class DownloadParameter(
    val merchantId: String,
    val ctms: String,
    val timeOut: String,
    val currencyCode: String,
    val countryCode: String,
    val callHomeTime: String,
    val merchantNameAndLocation: String,
    val merchantCategoryCode: String,
)
