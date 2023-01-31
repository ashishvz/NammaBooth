package com.payoman.nammabooth.repository

import com.payoman.nammabooth.database.Voter
import com.payoman.nammabooth.database.VoterOperation
import javax.inject.Inject

class GenealogyRepository @Inject constructor(
    private val voterOperation: VoterOperation
) {

    suspend fun getVotersOfFamily(houseNo: String, voterId: String): MutableList<Voter> {
        return voterOperation.getVoterListOfFamilyMember(houseNo, voterId)
    }

    suspend fun getVoterFromVoterId(voterId: String): Voter? {
        return voterOperation.getVoterFromVoterId(voterId)
    }
}