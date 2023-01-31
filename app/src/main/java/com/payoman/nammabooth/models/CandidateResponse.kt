package com.payoman.nammabooth.models

import com.payoman.nammabooth.database.Candidate
import java.util.Date

data class CandidateResponse(
    val data: Candidate,
    val status: String
)

data class CandidateDetails(
    val id: Int,
    val candidatePhoneNumber: String,
    val candidateName: String,
    val partyId: Int,
    val assemblyConstituencyNumber: String,
    val candidateImageUrl: String?,
    val partySymbolUrl: String?,
    val candidateManifestoUrl: String?,
    val bannerUrls: List<String>?,
    val newsLetterUrls: List<String>?,
    val urls: Urls?,
    val created_at: Date
)

data class Urls(
    val facebook: String
)
