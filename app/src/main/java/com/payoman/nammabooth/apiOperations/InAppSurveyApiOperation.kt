package com.payoman.nammabooth.apiOperations

import com.payoman.nammabooth.api.GetSurveyList
import com.payoman.nammabooth.models.InAppSurveyResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import retrofit2.Retrofit
import javax.inject.Inject

class InAppSurveyApiOperation @Inject constructor(
    private val retrofit: Retrofit
) {

    suspend fun getSurveyList(partyId: String, electionId: String): InAppSurveyResponse? {
        val retrofit = retrofit.create(GetSurveyList::class.java)
        return runBlocking(Dispatchers.IO) {
            retrofit.getSurvey(partyId, electionId).execute().body()
        }
    }
}