package com.payoman.nammabooth.repository

import com.payoman.nammabooth.apiOperations.ReportApiOperation
import com.payoman.nammabooth.models.ReportResponse

class ReportRepository(private val reportApiOperation: ReportApiOperation) {

    suspend fun getReports(partyId: String, electionId: String): ReportResponse? {
        return reportApiOperation.getReports(partyId, electionId)
    }
}