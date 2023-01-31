package com.payoman.nammabooth.models

data class VerifyOtpResponse(
    val token: String?,
    val partNos: String?,
    val assemblyConstituencyNumber: String?,
    val boothAgentName: String?,
    val status: String?,
    val message: String?
)
