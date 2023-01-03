package com.payoman.nammabooth.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.core.handlers.ReplaceFileCorruptionHandler
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.preferencesDataStoreFile
import com.payoman.nammabooth.database.UserOperation
import com.payoman.nammabooth.database.Voter
import com.payoman.nammabooth.database.VoterOperation
import com.payoman.nammabooth.repository.VoterRepository
import com.payoman.nammabooth.utils.Constants
import com.payoman.nammabooth.viewmodels.OtpViewModel
import com.payoman.nammabooth.viewmodels.VoterListViewModel
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import io.realm.RealmConfiguration
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
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

    @Provides
    @Singleton
    fun providePreferenceDataStore(@ApplicationContext appContext: Context): DataStore<Preferences> =
        PreferenceDataStoreFactory.create(
            corruptionHandler = ReplaceFileCorruptionHandler(
                produceNewData = { emptyPreferences() }
            ),
            scope = CoroutineScope(Dispatchers.IO),
            produceFile = {
                appContext.preferencesDataStoreFile(Constants.PREFERENCE_DATA_STORE_FILE_NAME)
            }
        )

    @Provides
    @Singleton
    fun provideOtpViewModel(dataStore: DataStore<Preferences>, voterOperation: VoterOperation): OtpViewModel {
        return OtpViewModel(dataStore, voterOperation)
    }

    @Provides
    @Singleton
    fun provideRealmInstance(): RealmConfiguration {
        return RealmConfiguration.Builder().name(Constants.DATABASE_NAME)
            .allowQueriesOnUiThread(true)
            .allowWritesOnUiThread(true)
            .schemaVersion(Constants.DB_SCHEMA_VERSION)
            .build()
    }

    @Provides
    @Singleton
    fun provideUserOperation(realmConfiguration: RealmConfiguration): UserOperation {
        return UserOperation(realmConfiguration)
    }

    @Provides
    @Singleton
    fun provideVoterOperation(realmConfiguration: RealmConfiguration): VoterOperation {
        return VoterOperation(realmConfiguration)
    }

    @Provides
    @Singleton
    fun provideVoterRepository(voterOperation: VoterOperation): VoterRepository {
        return VoterRepository(voterOperation)
    }

    @Provides
    @Singleton
    fun provideVoterListViewModel(voterRepository: VoterRepository, dataStore: DataStore<Preferences>): VoterListViewModel {
        return VoterListViewModel(voterRepository, dataStore)
    }
}