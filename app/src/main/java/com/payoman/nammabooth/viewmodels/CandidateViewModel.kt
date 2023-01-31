package com.payoman.nammabooth.viewmodels

import androidx.lifecycle.ViewModel
import com.payoman.nammabooth.database.Candidate
import com.payoman.nammabooth.repository.CandidateRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class CandidateViewModel @Inject constructor(private val candidateRepository: CandidateRepository): ViewModel() {

    suspend fun getCandidate(): MutableList<Candidate> {
        return candidateRepository.getCandidateDetails()
    }
}
