package com.judahben149.emvsync.utils

import java.text.SimpleDateFormat
import java.util.*

object DateUtils {

    fun transactionDate(): String {
        return SimpleDateFormat("yyMMdd", Locale.getDefault()).format(Date())
    }

    fun transactionTime(): String {
        return SimpleDateFormat("hhmmss", Locale.getDefault()).format(Date())
    }


}