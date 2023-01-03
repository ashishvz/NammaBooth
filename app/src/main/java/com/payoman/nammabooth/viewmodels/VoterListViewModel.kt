package com.payoman.nammabooth.viewmodels

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.payoman.nammabooth.database.Voter
import com.payoman.nammabooth.database.VoterOperation
import com.payoman.nammabooth.repository.VoterRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import org.bson.types.ObjectId
import javax.inject.Inject

@HiltViewModel
class VoterListViewModel @Inject constructor(private val voterRepository: VoterRepository, private val dataStore: DataStore<Preferences>): ViewModel() {

    suspend fun getVoterList(): MutableList<Voter> {
        return voterRepository.getVoterList()
    }

    fun provideDataStoreInstance(): DataStore<Preferences> {
        return dataStore
    }

    suspend fun updatePhoneNumber(voterId: String, phoneNumber: String) {
        voterRepository.updatePhoneNumber(voterId, phoneNumber)
    }

    suspend fun searchVoters(searchQuery: String): MutableList<Voter> {
        return voterRepository.getFilteredVoterList(searchQuery)
    }

}