package com.payoman.nammabooth.viewmodels

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.lifecycle.ViewModel
import com.payoman.nammabooth.database.Candidate
import com.payoman.nammabooth.database.CandidateOperation
import com.payoman.nammabooth.database.VoterOperation
import com.payoman.nammabooth.database.Voter
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class OtpViewModel @Inject constructor(
    private val dataStore: DataStore<Preferences>,
    private val voterOperation: VoterOperation,
    private val candidateOperation: CandidateOperation
) : ViewModel() {

    fun provideDataStoreInstance(): DataStore<Preferences> {
        return dataStore
    }

    suspend fun insertVoterList(voterList: List<Voter>) {
        voterOperation.insertVotersInTx(voterList)
    }

    suspend fun insertCandidate(candidate: Candidate) {
        candidateOperation.insertCandidate(candidate)
    }

    suspend fun deleteAllVoters() {
        voterOperation.deleteAllVoters()
    }

    suspend fun deleteAllData() {
        voterOperation.deleteAllData()
    }
}