package com.payoman.nammabooth.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.payoman.nammabooth.models.ReportAgeUiState
import com.payoman.nammabooth.models.SpinnerUiState
import com.payoman.nammabooth.repository.ReportAgeRepository
import com.payoman.nammabooth.repository.ReportColorRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ReportColorWiseViewModel @Inject constructor(private val reportColorRepository: ReportColorRepository) :
    ViewModel() {

    private val _uiState: MutableStateFlow<ReportAgeUiState> = MutableStateFlow(ReportAgeUiState())
    val uiState: Flow<ReportAgeUiState> get() = _uiState.asStateFlow()

    private suspend fun getVoterColorWise(color: String) {
        viewModelScope.launch(Dispatchers.IO) {
            reportColorRepository.getVoterColorWise(color).catch {
                it.printStackTrace()
            }.collect {
                _uiState.update { uiState ->
                    uiState.copy(voterList = it, isLoading = false)
                }
            }
        }
    }

    fun setColor(color: String) {
        viewModelScope.launch {
            getVoterColorWise(color)
        }
    }

}