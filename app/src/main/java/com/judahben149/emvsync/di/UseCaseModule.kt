package com.judahben149.emvsync.di

import android.content.SharedPreferences
import com.judahben149.emvsync.domain.usecase.KeyExchangeUseCase
import com.judahben149.emvsync.domain.usecase.TransactionUseCase
import com.judahben149.emvsync.utils.SessionManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object UseCaseModule {

    @Provides
    @Singleton
    fun providesTransactionUseCase(sessionManager: SessionManager): TransactionUseCase {
        return TransactionUseCase(sessionManager)
    }

    @Provides
    @Singleton
    fun providesKeyExchangeUseCase(sharedPreferences: SharedPreferences, sessionManager: SessionManager): KeyExchangeUseCase {
        return KeyExchangeUseCase(sharedPreferences, sessionManager)
    }
}