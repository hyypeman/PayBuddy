package com.judahben149.emvsync.utils

import com.nexgo.oaf.apiv3.DeviceEngine

object BeeperUtils {

    fun beepOnce(deviceEngine: DeviceEngine) {
        deviceEngine.beeper.beep(100)
    }

    fun beepOnceShort(deviceEngine: DeviceEngine) {
        deviceEngine.beeper.beep(50)
    }
}