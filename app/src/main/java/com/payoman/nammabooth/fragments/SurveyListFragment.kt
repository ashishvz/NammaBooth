package com.payoman.nammabooth.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.payoman.nammabooth.R
import com.payoman.nammabooth.adapters.SurveyAdapter
import com.payoman.nammabooth.adapters.SurveyListAdapter
import com.payoman.nammabooth.database.Voter
import com.payoman.nammabooth.databinding.DialogSurveyBinding
import com.payoman.nammabooth.interfaces.OnSurveyClickListener
import com.payoman.nammabooth.interfaces.OnVoterCardClickListener
import com.payoman.nammabooth.models.Survey
import com.payoman.nammabooth.models.TriggerSurveyRequest
import com.payoman.nammabooth.models.WhatsappMessage
import com.payoman.nammabooth.models.WhatsappMessageListResponse
import com.payoman.nammabooth.viewmodels.SurveyViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import retrofit2.http.POST

class SurveyListFragment: Fragment(), OnSurveyClickListener {

    private lateinit var binding: DialogSurveyBinding
    private val surveyViewModel: SurveyViewModel by activityViewModels()
    private var surveyAdapter: SurveyAdapter? = null
    private var whatsappMessageList: MutableList<Survey> = mutableListOf()
    private var selectedPosition = -1
    private var selectedPhoneNumber = 0L
    private val args: SurveyListFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.dialog_survey, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        selectedPhoneNumber = args.phoneNumber
        binding.apply {
            loadingProgressBar.show()
            surveyRecycler.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            surveyRecycler.setHasFixedSize(true)
            surveyAdapter = SurveyAdapter(requireContext(), whatsappMessageList, this@SurveyListFragment)
            surveyRecycler.adapter = surveyAdapter
            cancelButton.setOnClickListener {
                findNavController().navigate(SurveyListFragmentDirections.actionSurveyListFragmentToSurveyFragment())
            }
            submitSurveyButton.setOnClickListener {
                runBlocking(Dispatchers.IO) {
                    surveyViewModel.triggerSurvey(selectedPosition.toString(), TriggerSurveyRequest(
                        mutableListOf(selectedPhoneNumber)
                    ))
                }
            }
        }
        setObservers()
        runBlocking(Dispatchers.IO) {
            surveyViewModel.getSurveyList()
        }
    }

    private fun setObservers() {
        surveyViewModel.surveyList.observe(viewLifecycleOwner) {
            if (it != null && it.status == "SUCCESS" && it.data.isNotEmpty()) {
                whatsappMessageList.clear()
                for(survey in it.data) {
                    whatsappMessageList.add(Survey(survey, false))
                }
            }
            binding.loadingProgressBar.hide()
            surveyAdapter?.notifyDataSetChanged()
        }
        surveyViewModel.triggerSurvey.observe(viewLifecycleOwner) {
            if (it != null) {
                if (it.status == "PROCESSING") {
                    Toast.makeText(
                        requireContext(),
                        "Survey sent successfully!",
                        Toast.LENGTH_SHORT
                    ).show()
                    findNavController().popBackStack()
                    findNavController().popBackStack()
                    findNavController().navigate(R.id.surveyFragment)
                } else {
                    Toast.makeText(requireContext(), "Failed to send survey!", Toast.LENGTH_SHORT)
                        .show()
                }
            }
        }
    }

    override fun onClick(position: Int) {
        selectedPosition = whatsappMessageList[position].whatsappMessage.id
        for ((index) in whatsappMessageList.withIndex())
            whatsappMessageList[index].isSelected = position == index
    }

    override fun onDestroy() {
        surveyViewModel.surveyList.value = null
        surveyViewModel.triggerSurvey.value = null
        super.onDestroy()
    }
}