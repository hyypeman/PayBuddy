package com.judahben149.emvsync.utils

import android.content.SharedPreferences
import com.judahben149.emvsync.utils.Constants.ACQUIRING_INSTITUTION_ID
import com.judahben149.emvsync.utils.Constants.IP_ADDRESS
import com.judahben149.emvsync.utils.Constants.IS_FIRST_LAUNCH
import com.judahben149.emvsync.utils.Constants.PORT
import com.judahben149.emvsync.utils.Constants.TERMINAL_ID
import javax.inject.Inject

class PreferencesHelper @Inject constructor(private val sharedPreferences: SharedPreferences) {

    fun applyParameters() {
        sharedPreferences.apply {
            saveString(IP_ADDRESS, "206.71.17.51") // TODO: Change this to the URL
            saveString(PORT, "443")
            saveString(TERMINAL_ID, "NW16413")
            saveString(ACQUIRING_INSTITUTION_ID, "273970116") //042000314 Change the acquiring insitution ID for PAI metabank ACH routing 273970116
        }
    }

    fun isAppFirstLaunch(): Boolean {
        val isFirstLaunch = sharedPreferences.getBoolean(IS_FIRST_LAUNCH, true)

        if (isFirstLaunch) {
            return true
        } else {
            sharedPreferences.saveBoolean(IS_FIRST_LAUNCH, false)
            return false
        }
    }
}