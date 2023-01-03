package com.payoman.nammabooth.repository

import androidx.lifecycle.MutableLiveData
import com.payoman.nammabooth.database.Voter
import com.payoman.nammabooth.database.VoterOperation
import javax.inject.Inject

class VoterRepository @Inject constructor(private val voterOperation: VoterOperation) {

    suspend fun getVoterList(): MutableList<Voter> {
        return voterOperation.getVoterList()
    }

    suspend fun getFilteredVoterList(query: String) : MutableList<Voter> {
        return voterOperation.searchVoter(query)
    }

    suspend fun updatePhoneNumber(voterId: String, phoneNumber: String) {
        voterOperation.updateVoterPhoneNumber(voterId, phoneNumber)
    }
}