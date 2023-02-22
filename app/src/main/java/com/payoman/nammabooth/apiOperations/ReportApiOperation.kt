package com.payoman.nammabooth.apiOperations

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.payoman.nammabooth.api.GetReports
import com.payoman.nammabooth.api.GetSurveyList
import com.payoman.nammabooth.dataStore.TokenPreference
import com.payoman.nammabooth.dataStore.UserPreference
import com.payoman.nammabooth.models.ReportResponse
import com.payoman.nammabooth.utils.AppUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import retrofit2.Retrofit
import javax.inject.Inject

class ReportApiOperation @Inject constructor(private val retrofit: Retrofit,  private val dataStore: DataStore<Preferences>) {

    suspend fun getReports(partyId: String, electionId: String): ReportResponse? {
        val retrofit = retrofit.create(GetReports::class.java)
        val tokenPreference: TokenPreference =
            AppUtils.DataStorePreference.getTokenFromDataStorePreference(dataStore).first()
        val phoneNumber: UserPreference =
            AppUtils.DataStorePreference.getDataFromDataStorePreference(dataStore).first()
        return runBlocking(Dispatchers.IO) {
            retrofit.getReports(tokenPreference.token, partyId, electionId, phoneNumber.phoneNumber).execute().body()
        }
    }
}