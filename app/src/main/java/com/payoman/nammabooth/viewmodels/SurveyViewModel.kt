package com.payoman.nammabooth.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.payoman.nammabooth.models.DefaultResponse
import com.payoman.nammabooth.models.TriggerSurveyRequest
import com.payoman.nammabooth.models.WhatsappMessageListResponse
import com.payoman.nammabooth.repository.SurveyRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SurveyViewModel @Inject constructor(
    private val surveyRepository: SurveyRepository
): ViewModel() {

    val surveyList: MutableLiveData<WhatsappMessageListResponse> = MutableLiveData()
    val triggerSurvey: MutableLiveData<DefaultResponse> = MutableLiveData()

    suspend fun getSurveyList() {
        surveyList.postValue(surveyRepository.getSurveyList())
    }

    suspend fun triggerSurvey(id: String, triggerSurveyRequest: TriggerSurveyRequest) {
        triggerSurvey.postValue(surveyRepository.triggerSurvey(id, triggerSurveyRequest))
    }

    fun clearData() {

    }
}