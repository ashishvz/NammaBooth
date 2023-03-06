package com.payoman.nammabooth.repository

import com.payoman.nammabooth.apiOperations.ReportsLocalDataSource
import com.payoman.nammabooth.database.Voter
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ReportColorRepository @Inject constructor(private val reportsLocalDataSource: ReportsLocalDataSource) {
    suspend fun getVoterColorWise(color: String): Flow<MutableList<Voter>> {
        return reportsLocalDataSource.getVoterColorWise(color)
    }
}