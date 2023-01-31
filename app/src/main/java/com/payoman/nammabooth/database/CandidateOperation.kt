package com.payoman.nammabooth.database

import io.realm.Realm
import io.realm.RealmConfiguration
import io.realm.kotlin.executeTransactionAwait
import kotlinx.coroutines.Dispatchers
import javax.inject.Inject

class CandidateOperation @Inject constructor(
    private val realmConfiguration: RealmConfiguration
) {

    suspend fun insertCandidate(candidate: Candidate) {
        val realm = Realm.getInstance(realmConfiguration)
        realm.executeTransactionAwait(Dispatchers.IO) {
            it.insert(candidate)
        }
        realm.close()
    }

    suspend fun getCandidate(): MutableList<Candidate> {
        val realm = Realm.getInstance(realmConfiguration)
        var candidate = mutableListOf<Candidate>()
        realm.executeTransactionAwait(Dispatchers.IO) {
            candidate = it.copyFromRealm(it.where(Candidate::class.java).findAll())
        }
        realm.close()
        return candidate
    }
}