package com.payoman.nammabooth.models

import java.util.*

data class InAppSurveyResponse(
    val data: MutableList<InAppSurvey>,
    val status: String
)

data class InAppSurvey(
    val id: Int,
    val question: String,
    val options: MutableList<String>,
    val created_at: Date,
    val partyId: String,
    val electionId: String,
    var selected: String?,
)

data class InAppSurveyResult(
    var voterId: String,
    var questionId: Int,
    var answerSelected: String
)