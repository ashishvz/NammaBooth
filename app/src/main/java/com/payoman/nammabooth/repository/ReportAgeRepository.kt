package com.payoman.nammabooth.repository

import com.payoman.nammabooth.apiOperations.ReportsLocalDataSource
import com.payoman.nammabooth.database.Voter
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ReportAgeRepository @Inject constructor(private val reportsLocalDataSource: ReportsLocalDataSource) {

    suspend fun getVotersByAge(maxAge: Int, minAge: Int): Flow<MutableList<Voter>> {
        return reportsLocalDataSource.getVoterInAgeRange(maxAge, minAge)
    }
}