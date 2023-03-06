package com.payoman.nammabooth.models

data class ReportAgeUiState(
    val voterList: MutableList<com.payoman.nammabooth.database.Voter> = mutableListOf(),
    val isLoading: Boolean = true,
    val errorMessage: String? = null,
    val minAge: String? = null,
    val maxAge: String? = null
)
