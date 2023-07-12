package com.judahben149.emvsync.utils

import timber.log.Timber

fun String?.logThis(tag: String = "TAG") {
    Timber.tag(tag).d(this)
}

fun Int?.logThis(tag: String = "TAG") {
    Timber.tag(tag).d(this.toString())
}

