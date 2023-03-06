package com.payoman.nammabooth.models

data class UiState(
    val voterList: MutableList<com.payoman.nammabooth.database.Voter> = mutableListOf(),
    val isLoading: Boolean = true,
    val errorMessage: String? = null
)
