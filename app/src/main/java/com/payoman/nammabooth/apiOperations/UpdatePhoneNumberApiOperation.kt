package com.payoman.nammabooth.apiOperations

import com.payoman.nammabooth.api.UpdatePhoneNumberInterface
import com.payoman.nammabooth.database.UpdatePhoneNumber
import com.payoman.nammabooth.models.DefaultResponse
import com.payoman.nammabooth.utils.AppUtils
import com.payoman.nammabooth.viewmodels.DataStorePreferenceViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import javax.inject.Inject

class UpdatePhoneNumberApiOperation @Inject constructor(
    private val retrofit: Retrofit,
    private val dataStorePreferenceViewModel: DataStorePreferenceViewModel
){

    suspend fun updatePhoneNumber(updatePhoneNUmberList: List<UpdatePhoneNumber>): Response<DefaultResponse> {
        val updatePhoneNumberInterface = retrofit.create(UpdatePhoneNumberInterface::class.java)
        var token: String
        return runBlocking(Dispatchers.IO) {
            token = AppUtils.DataStorePreference.getTokenFromDataStorePreference(
                dataStorePreferenceViewModel.provideDataStoreInstance()
            ).first().token
            updatePhoneNumberInterface.updatePhoneNumber(token, updatePhoneNUmberList)
            /*updatePhoneNumberInterface.updatePhoneNumber(token, updatePhoneNUmberList)
                .enqueue(object : Callback<DefaultResponse> {
                    override fun onResponse(
                        call: Call<DefaultResponse>,
                        response: Response<DefaultResponse>
                    ) {
                        if (response.isSuccessful) {
                            if (response.body()?.status.equals("SUCCESS")) {
                                status = true
                            }
                        } else {
                            status = false
                        }
                    }

                    override fun onFailure(call: Call<DefaultResponse>, t: Throwable) {
                        status = false
                        t.printStackTrace()
                    }
                })*/
        }
    }
}