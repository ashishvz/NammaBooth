package com.payoman.nammabooth.repository

import com.payoman.nammabooth.apiOperations.ReportsLocalDataSource
import com.payoman.nammabooth.database.Voter
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ReportPhoneRepository @Inject constructor(private val reportsLocalDataSource: ReportsLocalDataSource) {

    suspend fun getPhoneNumberWiseVoter(): Flow<MutableList<Voter>> {
        return reportsLocalDataSource.getPhoneNumberWiseVoters()
    }

}