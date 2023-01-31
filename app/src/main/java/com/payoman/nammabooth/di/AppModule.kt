package com.payoman.nammabooth.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.core.handlers.ReplaceFileCorruptionHandler
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.preferencesDataStoreFile
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.payoman.nammabooth.apiOperations.SurveyApiOperation
import com.payoman.nammabooth.apiOperations.UpdatePhoneNumberApiOperation
import com.payoman.nammabooth.apiOperations.VoterApiOperation
import com.payoman.nammabooth.database.*
import com.payoman.nammabooth.repository.*
import com.payoman.nammabooth.utils.Constants
import com.payoman.nammabooth.viewmodels.*
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
    fun provideOtpViewModel(dataStore: DataStore<Preferences>, voterOperation: VoterOperation, candidateOperation: CandidateOperation): OtpViewModel {
        return OtpViewModel(dataStore, voterOperation, candidateOperation)
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
    fun provideVoterRepository(voterOperation: VoterOperation, voterApiOperation: VoterApiOperation): VoterRepository {
        return VoterRepository(voterOperation, voterApiOperation)
    }

    @Provides
    @Singleton
    fun provideVoterListViewModel(voterRepository: VoterRepository, dataStore: DataStore<Preferences>): VoterListViewModel {
        return VoterListViewModel(voterRepository, dataStore)
    }

    @Provides
    @Singleton
    fun provideDataStorePreferenceViewModel(dataStore: DataStore<Preferences>): DataStorePreferenceViewModel {
        return DataStorePreferenceViewModel(dataStore)
    }

    @Provides
    @Singleton
    fun provideCandidateOperation(realmConfiguration: RealmConfiguration): CandidateOperation {
        return CandidateOperation(realmConfiguration)
    }

    @Provides
    @Singleton
    fun provideHomeViewModel(realmConfiguration: RealmConfiguration, candidateOperation: CandidateOperation): HomeViewModel {
        return HomeViewModel(realmConfiguration, candidateOperation)
    }

    @Provides
    @Singleton
    fun provideCandidateRepository(candidateOperation: CandidateOperation): CandidateRepository {
        return CandidateRepository(candidateOperation)
    }

    @Provides
    @Singleton
    fun provideUpdatePhoneOperation(realmConfiguration: RealmConfiguration): UpdatePhoneOperation {
        return UpdatePhoneOperation(realmConfiguration)
    }

    @Provides
    @Singleton
    fun provideUpdatePhoneNumberRepository(updatePhoneOperation: UpdatePhoneOperation, updatePhoneNumberApiOperation: UpdatePhoneNumberApiOperation): UpdatePhoneNumberRepository {
        return UpdatePhoneNumberRepository(updatePhoneOperation, updatePhoneNumberApiOperation)
    }

    @Provides
    @Singleton
    fun provideUpdatePhoneNumberApiOperation(retrofit: Retrofit, dataStorePreferenceViewModel: DataStorePreferenceViewModel): UpdatePhoneNumberApiOperation {
        return UpdatePhoneNumberApiOperation(retrofit, dataStorePreferenceViewModel)
    }

    @Provides
    @Singleton
    fun provideVoterApiOperation(retrofit: Retrofit, dataStore: DataStore<Preferences>): VoterApiOperation {
        return VoterApiOperation(retrofit, dataStore)
    }

    @Provides
    @Singleton
    fun provideUpdatePhoneViewModel(updatePhoneNumberRepository: UpdatePhoneNumberRepository, voterRepository: VoterRepository): UpdatePhoneNumberViewModel {
        return UpdatePhoneNumberViewModel(updatePhoneNumberRepository, voterRepository)
    }

    @Provides
    @Singleton
    fun provideGenealogyRepository(voterOperation: VoterOperation): GenealogyRepository {
        return GenealogyRepository(voterOperation)
    }

    @Provides
    @Singleton
    fun provideGenealogyViewModel(genealogyRepository: GenealogyRepository): GenealogyViewModel {
        return GenealogyViewModel(genealogyRepository)
    }

    @Provides
    @Singleton
    fun provideSurveyApiOperation(retrofit: Retrofit): SurveyApiOperation {
        return SurveyApiOperation(retrofit)
    }

    @Provides
    @Singleton
    fun provideSurveyRepository(surveyApiOperation: SurveyApiOperation): SurveyRepository {
        return SurveyRepository(surveyApiOperation)
    }

    @Provides
    @Singleton
    fun provideSurveyViewModel(surveyRepository: SurveyRepository): SurveyViewModel {
        return SurveyViewModel(surveyRepository)
    }
}