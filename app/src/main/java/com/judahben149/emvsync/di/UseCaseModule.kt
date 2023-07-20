package com.judahben149.emvsync.di

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
}