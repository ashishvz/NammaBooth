package com.payoman.nammabooth.apiOperations

import com.payoman.nammabooth.database.Voter
import io.realm.Case
import io.realm.Realm
import io.realm.RealmConfiguration
import io.realm.Sort
import io.realm.kotlin.executeTransactionAwait
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import javax.inject.Inject

class ReportsLocalDataSource @Inject constructor(private val realmConfiguration: RealmConfiguration) {

    suspend fun getPhoneNumberWiseVoters(): Flow<MutableList<Voter>> {
        val realm = Realm.getInstance(realmConfiguration)
        var voterList = mutableListOf<Voter>()
        realm.executeTransactionAwait(Dispatchers.IO) {
            voterList = realm.copyFromRealm(realm.where(Voter::class.java).isNotEmpty("mobileNo").sort("sno", Sort.ASCENDING).findAll())
        }
        realm.close()
        return flowOf(voterList)
    }

    suspend fun getVoterInAgeRange(maxAge: Int, minAge: Int): Flow<MutableList<Voter>> {
        val realm = Realm.getInstance(realmConfiguration)
        var voterList = mutableListOf<Voter>()
        realm.executeTransactionAwait(Dispatchers.IO) {
            voterList = realm.copyFromRealm(realm.where(Voter::class.java).greaterThanOrEqualTo("age", minAge).lessThanOrEqualTo("age", maxAge).sort("age", Sort.ASCENDING).findAll())
        }
        realm.close()
        return flowOf(voterList)
    }

    suspend fun getVoterColorWise(color: String): Flow<MutableList<Voter>> {
        val realm = Realm.getInstance(realmConfiguration)
        var voterList = mutableListOf<Voter>()
        realm.executeTransactionAwait(Dispatchers.IO) {
            voterList = realm.copyFromRealm(realm.where(Voter::class.java).equalTo("selectedColor", color, Case.INSENSITIVE).sort("sno", Sort.ASCENDING).findAll())
        }
        realm.close()
        return flowOf(voterList)
    }

}