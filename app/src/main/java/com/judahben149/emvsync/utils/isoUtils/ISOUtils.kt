package com.judahben149.emvsync.utils.isoUtils

import com.judahben149.emvsync.domain.model.NIBSSPackager
import com.judahben149.emvsync.domain.model.card.TransactionData
import com.judahben149.emvsync.utils.cryptographyUtils.AESUtils
import com.judahben149.emvsync.utils.cryptographyUtils.TripleDESUtils
import com.judahben149.emvsync.utils.logThis
import org.jpos.iso.ISODate
import org.jpos.iso.ISOException
import org.jpos.iso.ISOMsg
import org.jpos.iso.ISOUtil
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.HashMap
import javax.crypto.Cipher
import javax.crypto.spec.SecretKeySpec


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

    fun generateRetrievalReferenceNumber(transDateTime: String?, stan: String): String {
        val date = ISODate.parseISODate(transDateTime)
        val hour = SimpleDateFormat("HH", Locale.getDefault()).format(date)

        val julianDate = ISODate.getJulianDate(date)
        return julianDate + hour + stan
    }

    /** Logs the available fields in the ISO message
     * @param data The packed ISO message. It will be unpacked in this function
     * @param packager The ISO packager to unpack the data
     **/

    fun parseAndLogIsoMessage(data: String, packager: NIBSSPackager): Map<String, String>? {
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

    /**
     * Communicates with the host and the host
     * sends back an encrypted terminal master key
     * We then use the clear text key components to decrypt it.
     *
     * I'm not sure whether we are able to connect to the host yet.
     */
    fun getDecryptedTMKFromHost(field53: String): String? {
        // Hardcoded from Andrew's ATM screenshot for now
        val componentA = "376BFB98AE2629A129A889BAF454495E"
        val componentB = "EC497FFB6B6D34013707CEB3B69E8080"
        val expectedKCV = "530A1C" // This may not be correct, if this part fails remove the verification
        val encryptedKey = field53.substring(0, 32) //This is the encrypted TMK from CTMS

        var plainKey: ByteArray? = null
        try {
            val combinedKey = combineKeys(componentA, componentB)
            if (!verifyCombinedKey(combinedKey, expectedKCV)) {
                throw Exception("Combined key verification failed")
            }
            val bytesCombinedKey = ISOUtil.hex2byte(combinedKey)
            val bytesEncryptedKeyFromCTMS = ISOUtil.hex2byte(encryptedKey)
            val cipherForKeyDecryption = TripleDESUtils(bytesCombinedKey)

            // Decrypt
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

//    private fun getXorOfGroupKey(): String {
//        val hostGroupKey = BuildConfig.HOST_GROUP_KEY
//        return AESUtils().decryptHexFormat(hostGroupKey).toString()
//    }

    fun parseTLV(response: String, Tag: String): String? {
        var resp = response
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

    fun buildField59(transactionData: TransactionData, terminalId: String): String {
        transactionData.echoData = terminalId.plus("-" + transactionData.rrn).plus("-" +
                convertDe7ToDateFormatYYYYMMDDHHMMSS(transactionData.datetime.toString())
        )

        return transactionData.echoData.toString()
    }

    private fun convertDe7ToDateFormatYYYYMMDDHHMMSS(de7: String): String {
        if (de7.isEmpty()) {
            return getCurrentDateFormatYYYYMMDDHHMMSS()
        }

        val date = ISODate.parseISODate(de7)
        val dateFormat = SimpleDateFormat("yyyyMMddHHmmss", Locale.getDefault())
        return dateFormat.format(date)
    }

    private fun getCurrentDateFormatYYYYMMDDHHMMSS(): String {
        val formatter = SimpleDateFormat("yyyyMMddHHmmss", Locale.getDefault())
        return formatter.format(Date())
    }

    fun getIsoTransDate(): String {
        return ISODate.getDate(Date())
    }

    fun getIsoTransTime(): String {
        return ISODate.getTime(Date())
    }

    fun getIsoTransDateTime(): String {
        return ISODate.getDateTime(Date())
    }

    fun combineKeys(componentA: String, componentB: String): String {
        val keyA = componentA.chunked(2).map { it.toInt(16) }
        val keyB = componentB.chunked(2).map { it.toInt(16) }
        val combinedKey = keyA.zip(keyB) { a, b -> a xor b }
        return combinedKey.joinToString("") { it.toString(16).padStart(2, '0') }.uppercase()
    }

    private fun verifyCombinedKey(combinedKey: String, expectedKCV: String): Boolean {
        val keyBytes = ISOUtil.hex2byte(combinedKey)
        val zeroBytes = ByteArray(8) { 0 }
        val cipher = Cipher.getInstance("DESede/ECB/NoPadding")
        val keySpec = SecretKeySpec(keyBytes, "DESede")
        cipher.init(Cipher.ENCRYPT_MODE, keySpec)
        val encryptedZeroBytes = cipher.doFinal(zeroBytes)
        val kcv = encryptedZeroBytes.take(3).toByteArray()
        return ISOUtil.hexString(kcv) == expectedKCV
    }


}