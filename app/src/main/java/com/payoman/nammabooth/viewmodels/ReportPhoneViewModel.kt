package com.payoman.nammabooth.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.payoman.nammabooth.models.UiState
import com.payoman.nammabooth.repository.ReportPhoneRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject
@HiltViewModel
class ReportPhoneViewModel @Inject constructor(private val reportPhoneRepository: ReportPhoneRepository) :
    ViewModel() {
    init {
        getVoterList()
    }

    private val _uiState: MutableStateFlow<UiState> = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> get() = _uiState

    private fun getVoterList() {
        viewModelScope.launch(Dispatchers.IO) {
            reportPhoneRepository.getPhoneNumberWiseVoter().catch {
                it.printStackTrace()
            }.collect {
                if (it.size > 0) {
                    _uiState.update { uiState ->
                        uiState.copy(voterList = it, isLoading = false)
                    }
                } else {
                    _uiState.update { uiState ->
                        uiState.copy(isLoading = false, errorMessage = "No Voters Found!")
                    }
                }
            }
        }
    }

}