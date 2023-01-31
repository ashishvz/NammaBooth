package com.payoman.nammabooth.database

import io.realm.Realm
import io.realm.RealmConfiguration
import io.realm.RealmList
import io.realm.kotlin.executeTransactionAwait
import kotlinx.coroutines.Dispatchers
import javax.inject.Inject

class UpdatePhoneOperation @Inject constructor(private val realmConfiguration: RealmConfiguration) {

    suspend fun insertUpdatePhoneNumber(updatePhoneNumber: UpdatePhoneNumber) {
        val realm = Realm.getInstance(realmConfiguration)
        realm.executeTransactionAwait(Dispatchers.IO) {
            it.insert(updatePhoneNumber)
        }
        realm.close()
    }

    suspend fun getAllUpdatePhoneNumber(): MutableList<UpdatePhoneNumber> {
        val realm = Realm.getInstance(realmConfiguration)
        val updateList = mutableListOf<UpdatePhoneNumber>()
        realm.executeTransactionAwait(Dispatchers.IO) {
            updateList.addAll(it.copyFromRealm(it.where(UpdatePhoneNumber::class.java).findAll()))
        }
        realm.close()
        return updateList
    }

    suspend fun deleteAllUpdatePhone() {
        val realm = Realm.getInstance(realmConfiguration)
        realm.executeTransactionAwait(Dispatchers.IO) {
            it.delete(UpdatePhoneNumber::class.java)
        }
        realm.close()
    }
}