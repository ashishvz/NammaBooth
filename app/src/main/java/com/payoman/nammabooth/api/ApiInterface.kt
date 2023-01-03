package com.payoman.nammabooth.api

import com.payoman.nammabooth.models.DefaultResponse
import com.payoman.nammabooth.models.VerifyOtpResponse
import com.payoman.nammabooth.models.Voter
import com.payoman.nammabooth.models.VoterResponse
import com.payoman.nammabooth.utils.Constants
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Query

interface GetOtpInterface {
    @POST(Constants.GET_OTP)
    fun getOtp(@Query("phone") phone: String): Call<DefaultResponse>
}

interface VerifyOtpInterface {
    @POST(Constants.VERIFY_OTP)
    fun verifyOtp(@Query("phone") phone: String, @Query("otp") otp: String): Call<VerifyOtpResponse>
}

interface GetVoterListInterface {
    @GET(Constants.GET_VOTERS)
    fun getVoterList(@Query("part_nos") part_nos: String, @Header("Authorization") token: String) : Call<VoterResponse>
}

interface UpdatePhoneNumberInterface {
    @PUT(Constants.UPDATE_PHONE)
    fun updatePhoneNumber(@Query("voterId") voterId: String, @Query("voterPhone") voterPhone: String, @Header("Authorization") token: String): Call<DefaultResponse>
}