package com.payoman.nammabooth.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.payoman.nammabooth.models.DefaultResponse
import com.payoman.nammabooth.models.InAppSurveyResponse
import com.payoman.nammabooth.models.InAppSurveyResult
import com.payoman.nammabooth.repository.InAppSurveyRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
@HiltViewModel
class InAppSurveyViewModel @Inject constructor(
    private val inAppSurveyRepository: InAppSurveyRepository
) : ViewModel() {

    val surveyList: MutableLiveData<InAppSurveyResponse> = MutableLiveData()
    val surveyResult: MutableLiveData<DefaultResponse> = MutableLiveData()

    suspend fun getSurveyList(partyId: String, electionId: String) {
       surveyList.postValue(inAppSurveyRepository.getSurveyList(partyId, electionId))
    }

    suspend fun sendSurveyResult(inAppSurveyResult: MutableList<InAppSurveyResult>, partyId: String, electionId: String) {
        surveyResult.postValue(inAppSurveyRepository.sendSurveyResult(inAppSurveyResult, partyId, electionId))
    }
}