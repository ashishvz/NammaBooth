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
import com.payoman.nammabooth.database.Voter
import com.payoman.nammabooth.databinding.FragmentReportColorBinding
import com.payoman.nammabooth.interfaces.OnVoterCardClickListener
import com.payoman.nammabooth.viewmodels.ReportColorWiseViewModel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.distinctUntilChanged

class ReportColorWiseFragment : Fragment(), OnVoterCardClickListener {

    private lateinit var binding: FragmentReportColorBinding
    private val viewModel: ReportColorWiseViewModel by activityViewModels()
    private lateinit var reportPhoneAdapter: ReportPhoneAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_report_color, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {
            val spinnerList = mutableListOf("Blue", "Green", "Yellow")
            val spinnerAdapter = ArrayAdapter(
                requireContext(),
                android.R.layout.simple_spinner_dropdown_item,
                spinnerList
            )
            colorSpinner.adapter = spinnerAdapter
            colorSpinner.onItemSelectedListener = object: AdapterView.OnItemSelectedListener {
                override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                   viewModel.setColor(colorSpinner.selectedItem.toString())
                }

                override fun onNothingSelected(p0: AdapterView<*>?) {}

            }
            reportPhoneAdapter = ReportPhoneAdapter(this@ReportColorWiseFragment)
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
                    if (it.voterList.isEmpty()) { reportPhoneAdapter.submitData(mutableListOf());showToast() }else reportPhoneAdapter.submitData(it.voterList)
                }
            }
        }
    }

    private fun showToast() {
        Toast.makeText(requireContext(), "No data found", Toast.LENGTH_SHORT).show()
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