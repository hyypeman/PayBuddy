package com.judahben149.emvsync.di

import com.judahben149.emvsync.domain.model.NIBSSPackager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object IsoModule {

    @Provides
    @Singleton
    fun providesNibssPackager(): NIBSSPackager {
        return NIBSSPackager()
    }
}