package com.payoman.nammabooth.models

data class SendSurveyStatusRequest(
    val voterId: String,
    val colorCode: String,
    val isVoted: Boolean,
    val isSurveyCompleted: Boolean
)
