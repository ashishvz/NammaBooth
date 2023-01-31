package com.payoman.nammabooth.repository

import androidx.lifecycle.MutableLiveData
import com.payoman.nammabooth.apiOperations.VoterApiOperation
import com.payoman.nammabooth.database.Voter
import com.payoman.nammabooth.database.VoterOperation
import com.payoman.nammabooth.models.DefaultResponse
import okhttp3.MultipartBody
import javax.inject.Inject

class VoterRepository @Inject constructor(
    private val voterOperation: VoterOperation,
    private val voterApiOperation: VoterApiOperation) {

    suspend fun getVoterList(): MutableList<Voter> {
        return voterOperation.getVoterList()
    }

    suspend fun getFilteredVoterList(query: String) : MutableList<Voter> {
        return voterOperation.searchVoter(query)
    }

    suspend fun updatePhoneNumber(voterId: String, phoneNumber: String) {
        voterOperation.updateVoterPhoneNumber(voterId, phoneNumber)
    }

    suspend fun fetchVotersListFromApi(): MutableList<Voter>? {
        return voterApiOperation.fetchVoterList()
    }

    suspend fun sendSlip(phoneNumber: String, pdfDoc: MultipartBody.Part): DefaultResponse? {
        return voterApiOperation.sendVoterSlip(phoneNumber, pdfDoc)
    }
}