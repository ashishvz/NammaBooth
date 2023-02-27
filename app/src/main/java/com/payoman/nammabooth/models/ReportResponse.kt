package com.payoman.nammabooth.models

import java.util.Date

data class ReportResponse(
    val data: MutableList<Reports>,
    val status: String
)

data class Reports(
    val id: Int,
    val voterId: String,
    val questionId: Int,
    val answerSelected: String,
    val createdAt: Date,
    val partyId: String,
    val electionId: String,
    val boothAgentPhoneNumber: String,
    val question: String
)
