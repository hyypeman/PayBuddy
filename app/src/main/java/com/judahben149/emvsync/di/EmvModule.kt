package com.judahben149.emvsync.di

import com.judahben149.emvsync.domain.model.keyExchange.HostConfiguration
import com.judahben149.emvsync.utils.Constants
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
}