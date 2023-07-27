package com.judahben149.emvsync.utils.emvUtils

import android.content.Context
import com.google.gson.Gson
import com.google.gson.JsonParser
import com.judahben149.emvsync.utils.logThis
import com.nexgo.oaf.apiv3.emv.AidEntity
import com.nexgo.oaf.apiv3.emv.AidEntryModeEnum
import com.nexgo.oaf.apiv3.emv.CapkEntity
import com.nexgo.oaf.apiv3.emv.EmvHandler2
import java.io.IOException
import javax.inject.Inject

class EmvUtils @Inject constructor(private val context: Context) {

    private fun getCapkList(): List<CapkEntity>? {
        val capkList: MutableList<CapkEntity> = ArrayList()
        val jsonArray = JsonParser.parseString(readAssetsText("emv_capk.json")).asJsonArray ?: return null

        for(user in jsonArray) {
            val userBean = Gson().fromJson(user, CapkEntity::class.java)
            capkList.add(userBean)
        }

        return capkList
    }

    fun getAidList(): List<AidEntity>? {
        val aidEntityList: MutableList<AidEntity> = ArrayList()
        val jsonArray = JsonParser.parseString(readAssetsText("inbas_aid.json")).asJsonArray ?: return null

        for(user in jsonArray) {
            val userBean = Gson().fromJson(user, AidEntity::class.java)
            val jsonObject = user.asJsonObject

            if (jsonObject != null && jsonObject["emvEntryMode"] != null) {
                val emvEntryMode = jsonObject["emvEntryMode"].asInt

                userBean.aidEntryModeEnum = AidEntryModeEnum.values()[emvEntryMode]
                "Emv entry mode - ${ userBean.aidEntryModeEnum }".logThis("Tagg")
            }
            aidEntityList.add(userBean)
        }

        return aidEntityList
    }


    fun initializeEmvAid(emvHandler2: EmvHandler2) {
        emvHandler2.delAllAid()

        if (emvHandler2.aidListNum <= 0) {
            val emvAidList = getAidList()

            if (emvAidList != null) {
                val initResult = emvHandler2.setAidParaList(emvAidList)
                "Init AID List result - $initResult".logThis("Tagg")
            } else {
                "Init AID List failed".logThis("Tagg")
                return
            }
        } else {
            "AID List already loaded".logThis("Tagg")
        }
    }

    fun initializeEmvCapk(emvHandler2: EmvHandler2) {
        emvHandler2.delAllCapk()

        if (emvHandler2.capkListNum <= 0) {
            val capkList = getCapkList()

            if (capkList != null) {
                val initResult = emvHandler2.setCAPKList(capkList)
                "Init CAPK List result - $initResult".logThis("Tagg")
            } else {
                "Init CAPK List failed".logThis("Tagg")
                return
            }
        } else {
            "CAPK List already loaded".logThis("Tagg")
        }
    }

    private fun readAssetsText(fileName: String): String? {

        try {
            val inputStream = context.assets.open(fileName)
            val size = inputStream.available()

            // Read the entire asset into a local byte buffer.
            val buffer = ByteArray(size)
            inputStream.read(buffer)
            inputStream.close()

            //convert the buffer into a string
            return String(buffer)
        } catch (ex: IOException) {
            ex.printStackTrace()
        }
        return null
    }

    fun maskCreditCardNumber(cardNumber: String): String {
        val startIndex = 7 // 8th digit
        val endIndex = 11 // 12th digit

        val maskedDigits = cardNumber.substring(startIndex, endIndex + 1).replace("\\d".toRegex(), "*")
        val maskedCardNumber = StringBuilder(cardNumber).apply {
            replace(startIndex, endIndex + 1, maskedDigits)
        }

        return maskedCardNumber.toString()
    }

    fun getAppLabel(
        issuerCodeTableIndex: String,
        appLabel: String?,
        appPreferredName: String
    ): String? {
        var appLabelResult: String? = null

        if (issuerCodeTableIndex.isEmpty()) {
            appLabelResult = appLabel
            return appLabelResult
        }

        if (issuerCodeTableIndex.toInt() == 1) {
            if (appPreferredName.isEmpty()) {
                appLabelResult = appLabel
            } else {
                appLabelResult = appPreferredName
            }
        } else {
            appLabelResult = appLabel
        }

        return appLabelResult
    }
}