package com.payoman.nammabooth.database

import io.realm.Realm
import io.realm.RealmConfiguration
import io.realm.kotlin.executeTransactionAwait
import kotlinx.coroutines.Dispatchers
import javax.inject.Inject

class UserOperation @Inject constructor(
    private val realmConfiguration: RealmConfiguration
) {

    suspend fun insertUser(user: User) {
        val realm = Realm.getInstance(realmConfiguration)
        realm.executeTransactionAwait(Dispatchers.IO)  { realmTransaction ->
            realmTransaction.insert(user)
        }
    }
}