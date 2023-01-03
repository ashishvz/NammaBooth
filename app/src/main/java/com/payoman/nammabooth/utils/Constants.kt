package com.payoman.nammabooth.utils

import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object Constants {
    const val BASE_URL: String = "http://3.111.180.161:8080/"
    const val GET_OTP: String = "auth/otp"
    const val VERIFY_OTP: String = "auth/v-otp"
    const val GET_VOTERS: String = "booth-agent/vd"
    const val UPDATE_PHONE: String = "booth-agent/voter"




    //Shared Preference
    const val PREFERENCE_DATA_STORE_FILE_NAME = "userDataStore"
    val PHONE_NUMBER = stringPreferencesKey("phoneNumber")
    val TOKEN = stringPreferencesKey("token")
    val PART_NO = stringPreferencesKey("partNos")
    val IS_LOGGED_IN = booleanPreferencesKey("isLoggedIn")


    //Database
    const val DATABASE_NAME = "NammaBooth"
    const val DB_SCHEMA_VERSION = 1L
}