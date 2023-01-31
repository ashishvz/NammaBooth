package com.payoman.nammabooth.database

import io.realm.Case
import io.realm.Realm
import io.realm.RealmConfiguration
import io.realm.kotlin.executeTransactionAwait
import kotlinx.coroutines.Dispatchers
import javax.inject.Inject

class VoterOperation @Inject constructor(
    private val realmConfiguration: RealmConfiguration
) {

    suspend fun insertVotersInTx(voterList: List<Voter>) {
        val realm = Realm.getInstance(realmConfiguration)
        realm.executeTransactionAwait(Dispatchers.IO) {
            it.insert(voterList)
        }
        realm.close()
    }

    suspend fun updateVoterPhoneNumber(voterId: String, phoneNumber: String): Boolean {
        val realm = Realm.getInstance(realmConfiguration)
        var isUpdated = false
        realm.executeTransactionAwait(Dispatchers.IO) {
            val voter = it.where(Voter::class.java).equalTo("voterId", voterId).findFirst()
            if (voter != null) {
                voter.mobileNo = phoneNumber
                it.copyToRealmOrUpdate(voter)
                isUpdated = true
            }
        }
        realm.close()
        return isUpdated
    }

    suspend fun getVoterList(): MutableList<Voter> {
        val realm = Realm.getInstance(realmConfiguration)
        var voterList = mutableListOf<Voter>()
        realm.executeTransactionAwait(Dispatchers.IO) {
            voterList = realm.copyFromRealm(realm.where(Voter::class.java).findAll())
        }
        realm.close()
        return voterList
    }

    suspend fun searchVoter(query: String): MutableList<Voter> {
        val realm = Realm.getInstance(realmConfiguration)
        val voters = realm.where(Voter::class.java)
        var voterList = mutableListOf<Voter>()
        realm.executeTransactionAwait(Dispatchers.IO) {
            val results = voters.contains("voterNameEn", query.trim(), Case.INSENSITIVE)
                .or()
                .contains("voterId", query.trim(), Case.INSENSITIVE).findAll()
            voterList = realm.copyFromRealm(results)
        }
        realm.close()
        return voterList
    }

    suspend fun getVoterListOfFamilyMember(houseNo: String, voterId: String): MutableList<Voter> {
        val realm = Realm.getInstance(realmConfiguration)
        val voters = realm.where(Voter::class.java)
        var voterList = mutableListOf<Voter>()
        realm.executeTransactionAwait(Dispatchers.IO) {
            val results = voters.equalTo("houseNo", houseNo.trim(), Case.INSENSITIVE)
                .and()
                .notEqualTo("voterId", voterId.trim()).findAll()
            voterList = realm.copyFromRealm(results)
        }
        realm.close()
        return voterList
    }

    suspend fun getVoterFromVoterId(voterId: String): Voter? {
        val realm = Realm.getInstance(realmConfiguration)
        val voters = realm.where(Voter::class.java)
        var voter: Voter? = null
        realm.executeTransactionAwait(Dispatchers.IO) {
            val results = voters.contains("voterId", voterId.trim(), Case.INSENSITIVE).findAll()
            voter = realm.copyFromRealm(results)[0]
        }
        realm.close()
        return voter
    }

    suspend fun deleteAllVoters() {
        val realm = Realm.getInstance(realmConfiguration)
        realm.executeTransactionAwait(Dispatchers.IO) {
            it.delete(Voter::class.java)
        }
        realm.close()
    }

    suspend fun deleteAllData() {
        val realm = Realm.getInstance(realmConfiguration)
        realm.executeTransactionAwait(Dispatchers.IO) {
            it.deleteAll()
        }
        realm.close()
    }
}