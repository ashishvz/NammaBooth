package com.payoman.nammabooth.api

import com.payoman.nammabooth.database.UpdatePhoneNumber
import com.payoman.nammabooth.models.*
import com.payoman.nammabooth.utils.Constants
import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part
import retrofit2.http.Query

interface GetOtpInterface {
    @POST(Constants.GET_OTP)
    fun getOtp(@Query("phone") phone: String): Call<DefaultResponse>
}

interface VerifyOtpInterface {
    @POST(Constants.VERIFY_OTP)
    fun verifyOtp(@Query("phone") phone: String, @Query("otp") otp: String, @Query("fcm") fcm: String?): Call<VerifyOtpResponse>
}

interface GetVoterListInterface {
    @GET(Constants.GET_VOTERS)
    fun getVoterList(@Query("part_nos") part_nos: String, @Header("Authorization") token: String) : Call<VoterResponse>
}

interface UpdatePhoneNumberInterface {
    @PUT(Constants.UPDATE_PHONE)
    @JvmSuppressWildcards
    suspend fun updatePhoneNumber(@Header("Authorization") token: String, @Body updatePhoneNumber: List<UpdatePhoneNumber>): Response<DefaultResponse>
}

interface GetCandidateDetails {
    @GET(Constants.CANDIDATE_DETAILS)
    fun getCandidateDetails(@Query("asc_no") ascNo: String, @Query("party_id") partyId: String, @Header("Authorization") token: String): Call<CandidateResponse>
}

interface RefreshToken {
    @POST(Constants.AUTH_TOKEN)
    fun refreshToken(@Query("phone") phoneNumber: String): Call<RefreshTokenResponse>
}

interface GetWhatsappMessage {
    @GET(Constants.WHATSAPP_MESSAGE)
    fun getWhatsappMessage(): Call<WhatsappMessageListResponse>
}

interface TriggerWhatsappMessage {
    @POST(Constants.TRIGGER_WHATSAPP_MESSAGE)
    fun triggerWhatsappMessage(@Query("id") id: String, @Body triggerSurveyRequest: TriggerSurveyRequest): Call<DefaultResponse>
}

interface SendVoterSlip {
    @Multipart
    @POST(Constants.SEND_WHATSAPP_SLIP)
    fun sendSlip(@Query("phone") phoneNumber: String, @Part pdfDoc: MultipartBody.Part): Call<DefaultResponse>
}