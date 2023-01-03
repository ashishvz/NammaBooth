package com.payoman.nammabooth.models

data class VoterResponse(
    val data: List<com.payoman.nammabooth.database.Voter>?,
    val size: Int?,
    val status: String?
)
