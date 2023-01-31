package com.payoman.nammabooth.utils

import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey

object Constants {
    const val BASE_URL: String = "http://3.111.180.161:8080/"
    const val WA_BASE_URL = "http://3.111.180.161:5000/"
    const val GET_OTP: String = "auth/otp"
    const val VERIFY_OTP: String = "auth/v-otp"
    const val GET_VOTERS: String = "booth-agent/vd"
    const val UPDATE_PHONE: String = "booth-agent/voter"
    const val CANDIDATE_DETAILS: String = "booth-agent/candidate"
    const val AUTH_TOKEN: String = "auth/token"
    const val WHATSAPP_MESSAGE = "whatsapp/message"
    const val TRIGGER_WHATSAPP_MESSAGE = "whatsapp/whatsapp/camp"
    const val SEND_WHATSAPP_SLIP = "whatsapp/e-slip"




    //Shared Preference
    const val PREFERENCE_DATA_STORE_FILE_NAME = "userDataStore"
    val PHONE_NUMBER = stringPreferencesKey("phoneNumber")
    val TOKEN = stringPreferencesKey("token")
    val PART_NO = stringPreferencesKey("partNos")
    val IS_LOGGED_IN = booleanPreferencesKey("isLoggedIn")
    val CONSTITUENCY_NUMBER = stringPreferencesKey("assemblyConstituencyNumber")
    val AGENT_NAME = stringPreferencesKey("boothAgentName")

    //Database
    const val DATABASE_NAME = "NammaBooth.realm"
    const val DB_SCHEMA_VERSION = 1L

    //Build version
    const val BUILD_VERSION = "v1.0"
}