package com.judahben149.emvsync.utils

import android.content.SharedPreferences
import com.judahben149.emvsync.BuildConfig
import com.judahben149.emvsync.utils.Constants.ACQUIRING_INSTITUTION_ID
import com.judahben149.emvsync.utils.Constants.HOST_URL
import com.judahben149.emvsync.utils.Constants.IS_FIRST_LAUNCH
import com.judahben149.emvsync.utils.Constants.TERMINAL_ID
import javax.inject.Inject

/**
 * This class is used to set the preferences
 * on first load.
 * The idea is that you can change the host/port etc.
 * from the terminal itself in the settings page.
 *
 * However, this currently would not work, because the comvelopes
 * would be different. And the only way to change the comvelopes
 * is for me to make changes in the local.properties file.
 */
class PreferencesHelper @Inject constructor(private val sharedPreferences: SharedPreferences) {
    fun applyParameters() {
        sharedPreferences.apply {
            saveString(HOST_URL, BuildConfig.HOST_URL) // Host URL is from Andrew
            saveString(TERMINAL_ID, "NW16413") // Terminal ID is from Andrew
            saveString(ACQUIRING_INSTITUTION_ID, "000000") // - I just changed it to 000 because PAI. 042000314 Change the acquiring insitution ID for PAI metabank ACH routing 273970116
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