package com.payoman.nammabooth.viewmodels

import androidx.lifecycle.ViewModel
import com.payoman.nammabooth.database.Candidate
import com.payoman.nammabooth.database.CandidateOperation
import dagger.hilt.android.lifecycle.HiltViewModel
import io.realm.Realm
import io.realm.RealmConfiguration
import io.realm.kotlin.executeTransactionAwait
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(private val realmConfiguration: RealmConfiguration, private val candidateOperation: CandidateOperation): ViewModel() {

    suspend fun getCandidateDetails(): MutableList<Candidate> {
        return candidateOperation.getCandidate()
    }
}