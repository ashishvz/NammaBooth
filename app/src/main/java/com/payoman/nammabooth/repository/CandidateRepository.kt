package com.payoman.nammabooth.repository

import com.payoman.nammabooth.database.Candidate
import com.payoman.nammabooth.database.CandidateOperation
import io.realm.Realm
import io.realm.RealmConfiguration
import io.realm.kotlin.executeTransactionAwait

class CandidateRepository(
    private val candidateOperation: CandidateOperation
) {

    suspend fun getNewsLetterList(): MutableList<String> {
        val list = candidateOperation.getCandidate()[0].newsLetterUrls
        return if (list != null && list.size > 0)
            list.toMutableList()
        else
            mutableListOf()
    }

    suspend fun getCandidateDetails(): MutableList<Candidate> {
        return candidateOperation.getCandidate()
    }
}