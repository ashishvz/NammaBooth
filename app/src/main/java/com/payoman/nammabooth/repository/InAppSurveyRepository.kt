package com.payoman.nammabooth.repository

import com.payoman.nammabooth.apiOperations.InAppSurveyApiOperation
import com.payoman.nammabooth.models.DefaultResponse
import com.payoman.nammabooth.models.InAppSurveyResponse
import com.payoman.nammabooth.models.InAppSurveyResult
import javax.inject.Inject

class InAppSurveyRepository @Inject constructor(
    private val inAppSurveyApiOperation: InAppSurveyApiOperation
) {

    suspend fun getSurveyList(partyId: String, electionId: String): InAppSurveyResponse? {
        return inAppSurveyApiOperation.getSurveyList(partyId, electionId)
    }

    suspend fun sendSurveyResult(inAppSurveyResult: MutableList<InAppSurveyResult>, partyId: String, electionId: String): DefaultResponse? {
        return inAppSurveyApiOperation.sendSurveyResult(inAppSurveyResult, partyId, electionId)
    }
}