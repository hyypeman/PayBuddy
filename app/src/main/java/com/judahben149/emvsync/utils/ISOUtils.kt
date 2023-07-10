package com.judahben149.emvsync.utils

import com.judahben149.emvsync.domain.model.NIBSSPackager
import org.jpos.iso.ISOException
import org.jpos.iso.ISOMsg
import org.jpos.iso.ISOUtil
import java.util.Random


fun getStan(): String? {
    var random: String? = "999999"

    try {
        random = ISOUtil.zeropad(Random().nextInt(999999).toString(), 6)
    } catch (e: ISOException) {
        e.message.logThis()
    }
    return random
}


fun parseResponse(data: String, packager: NIBSSPackager): Map<String, String>? {
    var hashMap = HashMap<String, String>()

    try {
        val message = ISOMsg()
        message.packager = packager
        message.unpack(data.toByteArray())
        "------ISO MESSAGE------".logThis()

        hashMap["0"] = message.mti
        message.mti.logThis()

        for (field in 1..message.maxField) {
            if (message.hasField(field)) {
                hashMap[field.toString()] = message.getString(field)
                "Field $field - ${message.getString(field)}".logThis()
            }
        }
    } catch (e: ISOException) {
        "Error: ${e.message.toString()}".logThis()
    } finally {
        "------------------------".logThis()
    }

    return hashMap
}

fun getDecryptedTMKFromHost(field53: String): String? {

}