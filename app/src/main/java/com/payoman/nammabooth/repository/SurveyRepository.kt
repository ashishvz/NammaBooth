package com.payoman.nammabooth.repository

import com.payoman.nammabooth.apiOperations.SurveyApiOperation
import com.payoman.nammabooth.models.DefaultResponse
import com.payoman.nammabooth.models.TriggerSurveyRequest
import com.payoman.nammabooth.models.WhatsappMessageListResponse
import javax.inject.Inject

class SurveyRepository @Inject constructor(
    private val surveyApiOperation: SurveyApiOperation
) {

    suspend fun getSurveyList(): WhatsappMessageListResponse? {
        return surveyApiOperation.getSurveyList()
    }

    suspend fun triggerSurvey(id: String, triggerSurveyRequest: TriggerSurveyRequest): DefaultResponse? {
        return surveyApiOperation.triggerWhatsappSurvey(id, triggerSurveyRequest)
    }
}