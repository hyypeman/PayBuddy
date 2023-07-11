package com.judahben149.emvsync.utils

import com.judahben149.emvsync.domain.model.NIBSSPackager
import com.judahben149.emvsync.utils.isoUtils.AESUtils
import com.judahben149.emvsync.utils.isoUtils.TripleDESUtils
import org.jpos.iso.ISOException
import org.jpos.iso.ISOMsg
import org.jpos.iso.ISOUtil
import java.util.Random

object ISOUtils {

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
            "-------ISO MESSAGE-------".logThis()

            hashMap["0"] = message.mti
            "MTI- ${message.mti}".logThis()

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
        val encryptedKey = field53.substring(0, 32) //This is the encrypted TMK from CTMS

        var plainKey: ByteArray? = null
        try {
            val bytesXORKeyComponents = ISOUtil.hex2byte(getXorOfGroupKey())
            val bytesEncryptedKeyFromCTMS = ISOUtil.hex2byte(encryptedKey)
            val cipherForKeyDecryption = TripleDESUtils(bytesXORKeyComponents)

            //decrypt
            plainKey = cipherForKeyDecryption.decode(bytesEncryptedKeyFromCTMS)
            "Plain key - ${plainKey.toString()}".logThis()

        } catch (ex: Exception) {
            "Error (Decrypting TMK) - ${ex.message.toString()}".logThis()
        }

        return ISOUtil.hexString(plainKey)
    }

    private fun getXorOfGroupKey(): String {
        val hostGroupKey =
            "4ADE7AE9AC74F9481144B6C06CAE81E3DEE8263EF515141599F32BB6BDB3B25A3CEE9C40579200C462EE5184469ABCC1"
        return AESUtils().decryptHexFormat(hostGroupKey).toString()
    }
}