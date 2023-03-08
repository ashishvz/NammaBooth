package com.payoman.nammabooth.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.payoman.nammabooth.models.ReportResponse
import com.payoman.nammabooth.repository.ReportRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ReportViewModel @Inject constructor(
    private val reportRepository: ReportRepository
): ViewModel() {

    val reports: MutableLiveData<ReportResponse> = MutableLiveData()

    suspend fun getReports(partyId: String, electionId: String) {
        reports.postValue(reportRepository.getReports(partyId, electionId))
    }

}