package com.payoman.nammabooth.utils

import android.content.Context
import android.content.pm.PackageManager
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import com.payoman.nammabooth.dataStore.IsLoggedIn
import com.payoman.nammabooth.dataStore.PartNoPreference
import com.payoman.nammabooth.dataStore.TokenPreference
import com.payoman.nammabooth.dataStore.UserPreference
import kotlinx.coroutines.flow.map
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


class AppUtils {
    object RetrofitClient {
        fun getInstance(): Retrofit {
            val mHttpLoggingInterceptor = HttpLoggingInterceptor()
                .setLevel(HttpLoggingInterceptor.Level.BODY)

            val mOkHttpClient = OkHttpClient
                .Builder()
                .addInterceptor(mHttpLoggingInterceptor)
                .build()

            return Retrofit.Builder()
                .baseUrl(Constants.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(mOkHttpClient)
                .build()
        }
    }

    object DataStorePreference {
        suspend fun saveDataToDataStorePreference(userPreference: UserPreference, dataStore: DataStore<Preferences>) {
            dataStore.edit {
                it[Constants.PHONE_NUMBER] = userPreference.phoneNumber
            }
        }

        fun getDataFromDataStorePreference(dataStore: DataStore<Preferences>): kotlinx.coroutines.flow.Flow<UserPreference> =
            dataStore.data.map {
                UserPreference(
                    phoneNumber = it[Constants.PHONE_NUMBER] ?: ""
                )
            }

        suspend fun saveTokenToDataStorePreference(tokenPreference: TokenPreference, dataStore: DataStore<Preferences>) {
            dataStore.edit {
                it[Constants.TOKEN] = tokenPreference.token
            }
        }

        fun getTokenFromDataStorePreference(dataStore: DataStore<Preferences>): kotlinx.coroutines.flow.Flow<TokenPreference> =
            dataStore.data.map {
                TokenPreference(
                    token = it[Constants.TOKEN] ?: ""
                )
            }

        suspend fun savePartNosToDataStorePreference(partNosPreference: PartNoPreference, dataStore: DataStore<Preferences>) {
            dataStore.edit {
                it[Constants.PART_NO] = partNosPreference.partNos
                it[Constants.CONSTITUENCY_NUMBER] = partNosPreference.constituencyNumber
                it[Constants.AGENT_NAME] = partNosPreference.boothAgentName
            }
        }

        fun getPartNosFromDataStorePreference(dataStore: DataStore<Preferences>): kotlinx.coroutines.flow.Flow<PartNoPreference> =
            dataStore.data.map {
                PartNoPreference(
                    partNos = it[Constants.PART_NO] ?: "",
                    constituencyNumber = it[Constants.CONSTITUENCY_NUMBER] ?: "",
                    boothAgentName = it[Constants.AGENT_NAME] ?: ""
                )
            }

        suspend fun saveIsLoggedInToDataStorePreference(isLoggedIn: IsLoggedIn, dataStore: DataStore<Preferences>) {
            dataStore.edit {
                it[Constants.IS_LOGGED_IN] = isLoggedIn.isLoggedIn
            }
        }

        fun getIsLoggedInFromDataStorePreference(dataStore: DataStore<Preferences>): kotlinx.coroutines.flow.Flow<IsLoggedIn> =
            dataStore.data.map {
                IsLoggedIn(
                    isLoggedIn = it[Constants.IS_LOGGED_IN] ?: false
                )
            }
    }

    object WhatsApp {
        fun whatsappInstalledOrNot(uri: String, context: Context): Boolean {
            val pm: PackageManager = context.packageManager
            var isInstalled = false
            isInstalled = try {
                pm.getPackageInfo(uri, PackageManager.GET_ACTIVITIES)
                true
            } catch (e: PackageManager.NameNotFoundException) {
                false
            }
            return isInstalled
        }
    }

    object InternetCheckUtils {
        fun isConnected(): Boolean {
            return try {
                val command = "ping -c 1 google.com"
                Runtime.getRuntime().exec(command).waitFor() == 0
            } catch (e: Exception) {
                e.printStackTrace();
                false
            }
        }
    }

}