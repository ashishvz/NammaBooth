package com.payoman.nammabooth.apiOperations

import com.payoman.nammabooth.api.GetWhatsappMessage
import com.payoman.nammabooth.api.TriggerWhatsappMessage
import com.payoman.nammabooth.models.DefaultResponse
import com.payoman.nammabooth.models.TriggerSurveyRequest
import com.payoman.nammabooth.models.WhatsappMessageListResponse
import com.payoman.nammabooth.utils.Constants
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import retrofit2.Retrofit
import retrofit2.create
import javax.inject.Inject

class SurveyApiOperation @Inject constructor(
    val retrofit: Retrofit
) {

    suspend fun getSurveyList(): WhatsappMessageListResponse? {
        val retrofit = retrofit.newBuilder().baseUrl(Constants.WA_BASE_URL).build()
        val getWhatsappMessage = retrofit.create(GetWhatsappMessage::class.java)
        return runBlocking(Dispatchers.IO) {
            getWhatsappMessage.getWhatsappMessage().execute().body()
        }
    }

    suspend fun triggerWhatsappSurvey(
        id: String,
        triggerSurveyRequest: TriggerSurveyRequest
    ): DefaultResponse? {
        val retrofit = retrofit.newBuilder().baseUrl(Constants.WA_BASE_URL).build()
        val triggerSurvey = retrofit.create(TriggerWhatsappMessage::class.java)
        return runBlocking(Dispatchers.IO) {
            triggerSurvey.triggerWhatsappMessage(id, triggerSurveyRequest).execute().body()
        }
    }
}