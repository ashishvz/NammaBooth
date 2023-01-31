package com.payoman.nammabooth.viewmodels

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.payoman.nammabooth.database.Voter
import com.payoman.nammabooth.database.VoterOperation
import com.payoman.nammabooth.models.DefaultResponse
import com.payoman.nammabooth.repository.VoterRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import okhttp3.MultipartBody
import org.bson.types.ObjectId
import javax.inject.Inject

@HiltViewModel
class VoterListViewModel @Inject constructor(
    private val voterRepository: VoterRepository,
    private val dataStore: DataStore<Preferences>
    ): ViewModel() {

    val voterList: MutableLiveData<MutableList<Voter>> = MutableLiveData()
    val sendSlip: MutableLiveData<DefaultResponse> = MutableLiveData()

    suspend fun getVoterList() {
        voterList.postValue(voterRepository.getVoterList())
    }

    suspend fun updatePhoneNumber(voterId: String, phoneNumber: String) {
        voterRepository.updatePhoneNumber(voterId, phoneNumber)
    }

    suspend fun searchVoters(searchQuery: String) {
        voterList.postValue(voterRepository.getFilteredVoterList(searchQuery))
    }

    suspend fun sendSlip(phoneNumber: String, pdfDoc: MultipartBody.Part) {
        sendSlip.postValue(voterRepository.sendSlip(phoneNumber, pdfDoc))
    }

}