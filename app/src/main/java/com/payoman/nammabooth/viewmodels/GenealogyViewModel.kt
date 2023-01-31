package com.payoman.nammabooth.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.payoman.nammabooth.database.Voter
import com.payoman.nammabooth.repository.GenealogyRepository
import com.payoman.nammabooth.repository.VoterRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class GenealogyViewModel @Inject constructor(
    private val genealogyRepository: GenealogyRepository
): ViewModel() {

    val familyList: MutableLiveData<MutableList<Voter>> = MutableLiveData()
    val voter: MutableLiveData<Voter?> = MutableLiveData()

    suspend fun getFamilyOfVoter(houseNo: String, voterId: String) {
        familyList.postValue(genealogyRepository.getVotersOfFamily(houseNo, voterId))
    }

    suspend fun getVoter(voterId: String) {
        voter.postValue(genealogyRepository.getVoterFromVoterId(voterId))
    }
}