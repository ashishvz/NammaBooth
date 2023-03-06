package com.payoman.nammabooth.fragments

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.payoman.nammabooth.R
import com.payoman.nammabooth.adapters.ReportPhoneAdapter
import com.payoman.nammabooth.adapters.VoterListAdapter
import com.payoman.nammabooth.database.Voter
import com.payoman.nammabooth.databinding.FragmentReportAgeBinding
import com.payoman.nammabooth.interfaces.OnVoterCardClickListener
import com.payoman.nammabooth.viewmodels.ReportAgeViewModel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch

class ReportAgeFragment : Fragment(), OnVoterCardClickListener {

    private lateinit var binding: FragmentReportAgeBinding
    private val viewModel: ReportAgeViewModel by activityViewModels()
    private lateinit var reportPhoneAdapter: ReportPhoneAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_report_age, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {
            maxAgeSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                    viewModel.setMaxAge(maxAgeSpinner.selectedItem.toString())
                }

                override fun onNothingSelected(p0: AdapterView<*>?) {}

            }
            minAgeSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                    viewModel.setMinAge(minAgeSpinner.selectedItem.toString())
                }

                override fun onNothingSelected(p0: AdapterView<*>?) {}

            }
            reportPhoneAdapter = ReportPhoneAdapter(this@ReportAgeFragment)
            recyclerView.layoutManager = LinearLayoutManager(requireContext())
            recyclerView.adapter = reportPhoneAdapter
        }
        collectUiData()
    }

    private fun collectUiData() {
        lifecycleScope.launchWhenStarted {
            viewModel.uiState.distinctUntilChanged().catch {
                it.printStackTrace()
            }.collect {
                binding.apply {
                    loadingProgressBar.isVisible = it.isLoading
                    reportPhoneAdapter.submitData(it.voterList)
                }
            }
        }
        lifecycleScope.launchWhenStarted {
            viewModel.spinnerUiState.distinctUntilChanged().collect {
                binding.apply {
                    val spinnerAdapter = ArrayAdapter(
                        requireContext(),
                        android.R.layout.simple_spinner_dropdown_item,
                        it.spinnerData
                    )
                    minAgeSpinner.adapter = spinnerAdapter
                    maxAgeSpinner.adapter = spinnerAdapter
                }
            }
        }
    }

    override fun OnClick(voter: Voter) {
        if (!voter.mobileNo.isNullOrBlank()) {
            val intent = Intent(Intent.ACTION_DIAL)
            intent.data = Uri.parse("tel:${voter.mobileNo}")
            startActivity(intent)
        } else {
            Toast.makeText(requireContext(), "No phone number", Toast.LENGTH_LONG).show()
        }
    }
}