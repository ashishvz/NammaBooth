package com.payoman.nammabooth.apiOperations

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.payoman.nammabooth.api.GetSurveyList
import com.payoman.nammabooth.api.SendSurveyResult
import com.payoman.nammabooth.api.SendVoterStatus
import com.payoman.nammabooth.dataStore.TokenPreference
import com.payoman.nammabooth.models.DefaultResponse
import com.payoman.nammabooth.models.InAppSurveyResponse
import com.payoman.nammabooth.models.InAppSurveyResult
import com.payoman.nammabooth.models.SendSurveyStatusRequest
import com.payoman.nammabooth.utils.AppUtils
import com.payoman.nammabooth.utils.Constants
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import retrofit2.Retrofit
import javax.inject.Inject

class InAppSurveyApiOperation @Inject constructor(
    private val retrofit: Retrofit,
    private val dataStore: DataStore<Preferences>
) {
    suspend fun getSurveyList(partyId: String, electionId: String): InAppSurveyResponse? {
        val retrofit = retrofit.create(GetSurveyList::class.java)
        val tokenPreference: TokenPreference =
            AppUtils.DataStorePreference.getTokenFromDataStorePreference(dataStore).first()
        return runBlocking(Dispatchers.IO) {
            retrofit.getSurvey(tokenPreference.token, partyId, electionId).execute().body()
        }
    }

    suspend fun sendSurveyResult(inAppSurveyResult: MutableList<InAppSurveyResult>, partyId: String, electionId: String): DefaultResponse? {
        val retrofit = retrofit.create(SendSurveyResult::class.java)
        val tokenPreference: TokenPreference =
            AppUtils.DataStorePreference.getTokenFromDataStorePreference(dataStore).first()
        return runBlocking(Dispatchers.IO) {
            retrofit.sendSurveyResult(tokenPreference.token, partyId, electionId, inAppSurveyResult).execute().body()
        }
    }

    suspend fun sendVoterStatus(sendSurveyStatusRequest: SendSurveyStatusRequest): DefaultResponse? {
        val retrofit = retrofit.create(SendVoterStatus::class.java)
        val tokenPreference: TokenPreference =
            AppUtils.DataStorePreference.getTokenFromDataStorePreference(dataStore).first()
        return runBlocking(Dispatchers.IO) {
            retrofit.sendVoterStatus(tokenPreference.token, Constants.PARTY_ID.toString(), Constants.ELECTION_ID.toString(), sendSurveyStatusRequest).execute().body()
        }
    }
}