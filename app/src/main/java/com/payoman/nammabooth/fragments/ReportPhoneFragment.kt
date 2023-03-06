package com.payoman.nammabooth.fragments

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.payoman.nammabooth.adapters.ReportPhoneAdapter
import com.payoman.nammabooth.database.Voter
import com.payoman.nammabooth.databinding.FragmentReportPhoneBinding
import com.payoman.nammabooth.interfaces.OnVoterCardClickListener
import com.payoman.nammabooth.viewmodels.ReportPhoneViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ReportPhoneFragment: Fragment(), OnVoterCardClickListener {
    private lateinit var binding: FragmentReportPhoneBinding
    private val viewModel: ReportPhoneViewModel by activityViewModels()
    private lateinit var reportPhoneAdapter: ReportPhoneAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentReportPhoneBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        reportPhoneAdapter = ReportPhoneAdapter(this)
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerView.adapter = reportPhoneAdapter
        collectData()
    }

    private fun collectData() {
        lifecycleScope.launch {
            viewModel.uiState.collect {
                binding.loadingProgressBar.isVisible = it.isLoading
                reportPhoneAdapter.submitData(it.voterList)
            }
        }
    }

    override fun OnClick(voter: Voter) {
       if (!voter.mobileNo.isNullOrBlank()) {
           val intent = Intent(Intent.ACTION_DIAL)
           intent.data = Uri.parse("tel:${voter.mobileNo}")
           startActivity(intent)
       }
    }
}