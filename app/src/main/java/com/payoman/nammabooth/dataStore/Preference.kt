package com.payoman.nammabooth.dataStore

data class TokenPreference(
    val token: String
)

data class UserPreference(
    val phoneNumber: String
)

data class PartNoPreference(
    val partNos: String
)

data class IsLoggedIn(
    val isLoggedIn: Boolean
)