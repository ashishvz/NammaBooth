package com.payoman.nammabooth.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.payoman.nammabooth.models.ReportAgeUiState
import com.payoman.nammabooth.models.SpinnerUiState
import com.payoman.nammabooth.repository.ReportAgeRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ReportAgeViewModel @Inject constructor(private val reportAgeRepository: ReportAgeRepository): ViewModel() {

    private val _uiState: MutableStateFlow<ReportAgeUiState> = MutableStateFlow(ReportAgeUiState())
    val uiState: Flow<ReportAgeUiState> get() = _uiState.asStateFlow()

    private val _spinnerUiState: MutableStateFlow<SpinnerUiState> = MutableStateFlow(SpinnerUiState())
    val spinnerUiState: Flow<SpinnerUiState> get() = _spinnerUiState.asStateFlow()

    init {
        setAgeRange()
    }

    private suspend fun getVotersWithRange(maxAge: String, minAge: String) {
        viewModelScope.launch(Dispatchers.IO) {
            reportAgeRepository.getVotersByAge(maxAge.toInt(), minAge.toInt()).catch {
                it.printStackTrace()
            }.collect {
                _uiState.update { uiState->
                    uiState.copy(voterList = it, isLoading = false)
                }
            }
        }
    }

    private fun getVotersWithRange() {
        viewModelScope.launch {
            getVotersWithRange(_uiState.value.maxAge.toString(), _uiState.value.minAge.toString())
        }
    }

    private fun setAgeRange() {
        val ageRangeList = mutableListOf<String>()
        for (i in 18..100)
            ageRangeList.add(i.toString())
        viewModelScope.launch {
            _uiState.update {
                it.copy(isLoading = false)
            }
            _spinnerUiState.update {
                it.copy(spinnerData = ageRangeList)
            }
        }
    }

    fun setMaxAge(age: String) {
        viewModelScope.launch {
            _uiState.update {
                it.copy(maxAge = age, isLoading = false)
            }
        }
        getVotersWithRange()
    }

    fun setMinAge(age: String) {
        viewModelScope.launch {
            _uiState.update {
                it.copy(minAge = age, isLoading = false)
            }
        }
    }
}