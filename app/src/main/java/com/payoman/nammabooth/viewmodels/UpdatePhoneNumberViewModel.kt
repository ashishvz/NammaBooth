package com.payoman.nammabooth.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.payoman.nammabooth.database.UpdatePhoneNumber
import com.payoman.nammabooth.database.Voter
import com.payoman.nammabooth.models.DefaultResponse
import com.payoman.nammabooth.repository.UpdatePhoneNumberRepository
import com.payoman.nammabooth.repository.VoterRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import retrofit2.Response
import javax.inject.Inject
@HiltViewModel
class UpdatePhoneNumberViewModel @Inject constructor(
    private val updatePhoneNumberRepositroy: UpdatePhoneNumberRepository,
    private val voterRepository: VoterRepository
): ViewModel() {

    val voterList: MutableLiveData<MutableList<Voter>> = MutableLiveData()

    suspend fun getVoterList() {
        voterList.postValue(voterRepository.getVoterList())
    }

    suspend fun searchVoters(searchQuery: String) {
        voterList.postValue(voterRepository.getFilteredVoterList(searchQuery))
    }

    suspend fun insertUpdatePhoneNumber(updatePhoneNumber: UpdatePhoneNumber) {
        updatePhoneNumberRepositroy.insertUpdatePhoneNumber(updatePhoneNumber)
    }

    suspend fun postUpdatePhoneNumber(updatePhoneNumber: MutableList<UpdatePhoneNumber>): Response<DefaultResponse> {
        return updatePhoneNumberRepositroy.postUpdatePhoneNumber(updatePhoneNumber)
    }

    suspend fun updatePhoneNumber(voterId: String, phoneNumber: String) {
        voterRepository.updatePhoneNumber(voterId, phoneNumber)
    }
}