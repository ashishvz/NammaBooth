package com.payoman.nammabooth.interfaces

import com.payoman.nammabooth.repository.UpdatePhoneNumberRepository
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@EntryPoint
@InstallIn(SingletonComponent::class)
interface WorkerDependencyProvider {
    fun updatePhoneRepo(): UpdatePhoneNumberRepository
}