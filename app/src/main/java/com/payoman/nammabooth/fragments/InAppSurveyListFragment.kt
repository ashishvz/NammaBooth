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
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.payoman.nammabooth.R
import com.payoman.nammabooth.adapters.InAppSurveyAdapter
import com.payoman.nammabooth.databinding.FragmentInAppSurveyListBinding
import com.payoman.nammabooth.models.InAppSurveyResult
import com.payoman.nammabooth.utils.Constants
import com.payoman.nammabooth.viewmodels.InAppSurveyViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class InAppSurveyListFragment: Fragment() {

    private lateinit var binding: FragmentInAppSurveyListBinding
    private val viewModel: InAppSurveyViewModel by activityViewModels()
    private val args: InAppSurveyListFragmentArgs by navArgs()
    private lateinit var voterIdArgs: String
    private lateinit var inAppSurveyAdapter: InAppSurveyAdapter
    private val resultList: MutableList<InAppSurveyResult> = mutableListOf()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_in_app_survey_list, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {
            voterIdArgs = args.voterId
            loadingProgressBar.show()
            noData.visibility = View.GONE
            surveyRecyclerView.layoutManager = LinearLayoutManager(requireContext())
            inAppSurveyAdapter = InAppSurveyAdapter(requireContext(), mutableListOf())
            surveyRecyclerView.adapter = inAppSurveyAdapter
            submitSurveyButton.setOnClickListener {
                resultList.clear()
                for (item in inAppSurveyAdapter.getData()) {
                    if (item.selected != null) {
                        resultList.add(InAppSurveyResult(voterIdArgs, item.id, item.selected!!))
                    } else {
                        Toast.makeText(context, "Answer all questions", Toast.LENGTH_SHORT).show()
                        break
                    }
                }
                loadingProgressBar.show()
                lifecycleScope.launch(Dispatchers.IO) {
                    viewModel.sendSurveyResult(resultList, Constants.PARTY_ID.toString(), Constants.ELECTION_ID.toString())
                }
            }
        }
        setObservers()
        lifecycleScope.launch(Dispatchers.IO) {
            viewModel.getSurveyList(Constants.PARTY_ID.toString(), Constants.ELECTION_ID.toString())
        }
    }

    private fun setObservers() {
        viewModel.surveyList.observe(viewLifecycleOwner) {
            if (it != null  && it.status == "SUCCESS") {
                binding.loadingProgressBar.hide()
                if (it.data.size > 0) {
                    inAppSurveyAdapter.addData(it.data)
                } else {
                    binding.apply {
                        loadingProgressBar.hide()
                        noData.visibility = View.VISIBLE
                        submitSurveyButton.isEnabled = false
                    }
                }
            } else {
                binding.apply {
                    loadingProgressBar.hide()
                    noData.visibility = View.VISIBLE
                    submitSurveyButton.isEnabled = false
                }
            }
        }

        viewModel.surveyResult.observe(viewLifecycleOwner) {
            binding.loadingProgressBar.hide()
            if (it != null) {
                if (it.status == "PROCESSING") {
                    Toast.makeText(requireContext(), "Survey submitted", Toast.LENGTH_SHORT).show()
                    findNavController().popBackStack()
                } else {
                    Toast.makeText(requireContext(), "Failed to submit survey", Toast.LENGTH_SHORT)
                        .show()
                }
            }
        }
    }

    override fun onStop() {
        super.onStop()
        viewModel.surveyResult.postValue(null)
        viewModel.surveyList.removeObservers(viewLifecycleOwner)
        viewModel.surveyResult.removeObservers(viewLifecycleOwner)
    }
}