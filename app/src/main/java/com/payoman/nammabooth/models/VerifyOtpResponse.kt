package com.payoman.nammabooth.models

data class VerifyOtpResponse(
    val token: String?,
    val partNos: String?,
    val status: String?,
    val message: String?
)
