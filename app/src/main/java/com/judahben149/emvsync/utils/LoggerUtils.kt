package com.judahben149.emvsync.utils

import timber.log.Timber

fun String?.logThis() {
    Timber.tag("TAG").d(this)
}

fun Int?.logThis() {
    Timber.tag("TAG").d(this.toString())
}

