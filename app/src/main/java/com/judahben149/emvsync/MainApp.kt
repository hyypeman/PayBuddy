package com.judahben149.emvsync

import android.app.Application
import com.nexgo.oaf.apiv3.APIProxy
import com.nexgo.oaf.apiv3.DeviceEngine
import com.nexgo.oaf.apiv3.emv.EmvHandler2
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber

@HiltAndroidApp
class MainApp : Application() {

    lateinit var deviceEngine: DeviceEngine
    lateinit var emvHandler2: EmvHandler2

    override fun onCreate() {
        super.onCreate()
        Timber.plant(Timber.DebugTree())
        deviceEngine = APIProxy.getDeviceEngine(this)
        emvHandler2 = deviceEngine.getEmvHandler2("app2")

    }
}