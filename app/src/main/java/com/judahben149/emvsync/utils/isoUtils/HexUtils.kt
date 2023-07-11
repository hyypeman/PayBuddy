package com.judahben149.emvsync.utils

object HexUtils {

    fun hexStringToByteArray(str: String): ByteArray? {
        val len = str.length
        val data = ByteArray(len / 2)

        var i = 0
        while (i < len) {
            data[i / 2] =
                ((Character.digit(str[i], 16) shl 4) + Character.digit(str[i + 1], 16)).toByte()
            i += 2
        }

        return data
    }
}