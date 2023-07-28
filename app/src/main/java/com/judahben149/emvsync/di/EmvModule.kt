package com.judahben149.emvsync.di

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import com.judahben149.emvsync.MainApp
import com.judahben149.emvsync.domain.model.keyExchange.HostConfiguration
import com.judahben149.emvsync.domain.usecase.KeyExchangeUseCase
import com.judahben149.emvsync.utils.Constants
import com.judahben149.emvsync.utils.SessionManager
import com.judahben149.emvsync.utils.emvUtils.EmvUtils
import com.nexgo.oaf.apiv3.DeviceEngine
import com.nexgo.oaf.apiv3.emv.EmvHandler2
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object EmvModule {

    @Provides
    @Singleton
    fun providesHostConfiguration(): HostConfiguration {
        return HostConfiguration(
            ipAddress = Constants.IP_ADDRESS,
            port = Constants.PORT,
            terminalId = Constants.TERMINAL_ID,
            compKey1 = Constants.COMPONENT_KEY_1,
            compKey2 = Constants.COMPONENT_KEY_2,
            isSsl = Constants.IS_SSL_BOOL,
            timeout = Constants.TIMEOUT
        )
    }

    @Provides
    @Singleton
    fun providesDeviceEngine(application: Application): DeviceEngine {
        val mainApp = application as MainApp
        return mainApp.deviceEngine
    }

    @Provides
    @Singleton
    fun providesEmvHandler2(application: Application): EmvHandler2 {
        val mainApp = application as MainApp
        return mainApp.emvHandler2
    }

    @Provides
    @Singleton
    fun providesEmvUtils(context: Context): EmvUtils {
        return EmvUtils(context)
    }
}