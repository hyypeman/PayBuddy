package com.judahben149.emvsync.presentation.settings

data class SettingsState(
    val hostIpAddress: String = "",
    val portNo: String = "",
    val terminalNumber: String = "",
    val acquiringInstitutionCode: String = "",
)