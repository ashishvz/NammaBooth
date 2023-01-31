package com.payoman.nammabooth.apiOperations

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.payoman.nammabooth.api.GetVoterListInterface
import com.payoman.nammabooth.api.SendVoterSlip
import com.payoman.nammabooth.dataStore.PartNoPreference
import com.payoman.nammabooth.dataStore.TokenPreference
import com.payoman.nammabooth.database.Voter
import com.payoman.nammabooth.models.DefaultResponse
import com.payoman.nammabooth.models.VoterResponse
import com.payoman.nammabooth.utils.AppUtils
import com.payoman.nammabooth.utils.Constants
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import okhttp3.MultipartBody
import retrofit2.*
import javax.inject.Inject

class VoterApiOperation @Inject constructor(
    private val retrofit: Retrofit,
    private val dataStore: DataStore<Preferences>
) {
    suspend fun fetchVoterList(): MutableList<Voter>? {
        val getVoterListInterface = retrofit.create(GetVoterListInterface::class.java)
        var voterList: MutableList<Voter>? = mutableListOf()
        val partPreference: PartNoPreference =
            AppUtils.DataStorePreference.getPartNosFromDataStorePreference(dataStore).first()
        val tokenPreference: TokenPreference =
            AppUtils.DataStorePreference.getTokenFromDataStorePreference(dataStore).first()
        getVoterListInterface.getVoterList(partPreference.partNos, tokenPreference.token)
            .enqueue(object : Callback<VoterResponse> {
                override fun onResponse(
                    call: Call<VoterResponse>,
                    response: Response<VoterResponse>
                ) {
                    if (response.isSuccessful) {
                        val voterResponse = response.body()
                        if (voterResponse != null && voterResponse.status.equals("SUCCESS")) {
                            if (voterResponse.data != null)
                                voterList?.addAll(voterResponse.data.toMutableList())
                            else
                                voterList?.addAll(mutableListOf())
                        }
                    }
                }

                override fun onFailure(call: Call<VoterResponse>, t: Throwable) {
                    voterList = null
                    t.printStackTrace()
                }
            })
        return voterList
    }

    suspend fun sendVoterSlip(phoneNumber: String, pdfDoc: MultipartBody.Part): DefaultResponse? {
        val retrofit = retrofit.newBuilder().baseUrl(Constants.WA_BASE_URL).build()
        val sendVoterSlip = retrofit.create(SendVoterSlip::class.java)
        return runBlocking(Dispatchers.IO) {
            sendVoterSlip.sendSlip(phoneNumber, pdfDoc).execute().body()
        }
    }
}