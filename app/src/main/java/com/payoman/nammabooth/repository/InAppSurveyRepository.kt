package com.payoman.nammabooth.repository

import com.payoman.nammabooth.apiOperations.InAppSurveyApiOperation
import com.payoman.nammabooth.database.VoterOperation
import com.payoman.nammabooth.models.DefaultResponse
import com.payoman.nammabooth.models.InAppSurveyResponse
import com.payoman.nammabooth.models.InAppSurveyResult
import com.payoman.nammabooth.models.SendSurveyStatusRequest
import javax.inject.Inject

class InAppSurveyRepository @Inject constructor(
    private val inAppSurveyApiOperation: InAppSurveyApiOperation,
    private val voterOperation: VoterOperation
) {

    suspend fun getSurveyList(partyId: String, electionId: String): InAppSurveyResponse? {
        return inAppSurveyApiOperation.getSurveyList(partyId, electionId)
    }

    suspend fun sendSurveyResult(inAppSurveyResult: MutableList<InAppSurveyResult>, partyId: String, electionId: String): DefaultResponse? {
        return inAppSurveyApiOperation.sendSurveyResult(inAppSurveyResult, partyId, electionId)
    }

    suspend fun sendVoterStatus(sendSurveyStatusRequest: SendSurveyStatusRequest): DefaultResponse? {
        return inAppSurveyApiOperation.sendVoterStatus(sendSurveyStatusRequest)
    }

    suspend fun saveSelectedColor(voterId: String, color: String) {
        return voterOperation.setSelectedColor(voterId, color)
    }
}