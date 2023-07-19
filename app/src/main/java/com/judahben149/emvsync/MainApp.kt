package com.judahben149.emvsync

import android.app.Application
import com.judahben149.emvsync.utils.emvUtils.EmvUtils
import com.judahben149.emvsync.utils.logThis
import com.nexgo.oaf.apiv3.APIProxy
import com.nexgo.oaf.apiv3.DeviceEngine
import com.nexgo.oaf.apiv3.emv.EmvHandler2
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber
import javax.inject.Inject

@HiltAndroidApp
class MainApp : Application() {

    lateinit var deviceEngine: DeviceEngine
    lateinit var emvHandler2: EmvHandler2

    @Inject
    lateinit var emvUtils: EmvUtils

    override fun onCreate() {
        super.onCreate()
        Timber.plant(Timber.DebugTree())
        deviceEngine = APIProxy.getDeviceEngine(this)
        emvHandler2 = deviceEngine.getEmvHandler2("app2")

        emvUtils.initializeEmvAid(emvHandler2)
        emvUtils.initializeEmvCapk(emvHandler2)
    }
}