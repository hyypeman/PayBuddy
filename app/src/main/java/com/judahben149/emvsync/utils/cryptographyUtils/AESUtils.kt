package com.judahben149.emvsync.utils.cryptographyUtils

import android.util.Base64
import com.judahben149.emvsync.utils.HexUtils
import javax.crypto.Cipher
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec

class AESUtils {

    private val ENCRYPTION_KEY = "7014E5C3D986AF89FD254AF132D5D565"
    private val ENCRYPTION_IV = "E66A42798C087E60"
    private val key = ENCRYPTION_KEY.toByteArray()
    private val IV = ENCRYPTION_IV.toByteArray() //initialization vector


    fun decryptHexFormat(cipherHex: String): String? {
        val decodedHex = HexUtils.hexStringToByteArray(cipherHex)
        val cipherText = Base64.encodeToString(decodedHex, Base64.NO_WRAP)

        return try {
            //Get cipher instance
            val cipher = Cipher.getInstance("AES/CBC/PKCS5Padding")

            //create secret keyspec
            val keySpec = SecretKeySpec(key, "AES")

            //Create Initialization vector parameter
            val ivSPec = IvParameterSpec(IV)

            //Initialize cipher for decrypt mode
            cipher.init(Cipher.DECRYPT_MODE, keySpec, ivSPec)

            //Perform decryption
            val decryptedText = cipher.doFinal(Base64.decode(cipherText, Base64.NO_WRAP))

            String(decryptedText)

        } catch (ex: Exception) {
            throw RuntimeException(ex)
        }
    }
}