package com.judahben149.emvsync.utils

import com.judahben149.emvsync.BuildConfig
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

    /** Logs the available fields in the ISO message
     * @param data The packed ISO message. It will be unpacked in this function
     * @param packager The ISO packager to unpack the data
     **/

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

    fun getDecryptedTSKFromHost(field53: String, masterKey: String?): String? {
        val encryptedKey = field53.substring(0, 32) //This is the encrypted TSK from CTMS

        var plainKey: ByteArray? = null

        try {
            val bytesXORKeyComponents = ISOUtil.hex2byte(masterKey)
            val bytesEncryptedKeyFromCTMS = ISOUtil.hex2byte(encryptedKey)
            val cipherForKeyDecryption = TripleDESUtils(bytesXORKeyComponents)

            //decrypt
            plainKey = cipherForKeyDecryption.decode(bytesEncryptedKeyFromCTMS)

        } catch (ex: Exception) {
            "Error (Decrypting TSK) - ${ex.message.toString()}".logThis()
        }

        return ISOUtil.hexString(plainKey)
    }

    private fun getXorOfGroupKey(): String {
        val hostGroupKey = BuildConfig.HOST_GROUP_KEY
        return AESUtils().decryptHexFormat(hostGroupKey).toString()
    }

    fun parseTLV(resp: String, Tag: String): String? {
        var resp = resp
        var data = ""
        var len = ""
        val nextTag = 0
        for (i in 0 until resp.length) {
            val tag = resp.substring(nextTag, nextTag + Tag.length)
            if (tag == Tag) {
                len = resp.substring(Tag.length, Tag.length + 3)
                data = resp.substring(5, 5 + len.toInt())
                return data
            }
            len = resp.substring(2, 5)
            data = resp.substring(5, 5 + len.toInt())
            resp = resp.substring(data.length + 5)
        }
        return data
    }
}