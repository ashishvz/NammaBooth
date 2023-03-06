package com.payoman.nammabooth.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.payoman.nammabooth.R
import com.payoman.nammabooth.adapters.ReportFragmentAdapter
import com.payoman.nammabooth.databinding.FragmentReportBinding
import com.payoman.nammabooth.databinding.FragmentReportsBinding
import com.payoman.nammabooth.models.Reports
import com.payoman.nammabooth.utils.Constants
import com.payoman.nammabooth.viewmodels.ReportViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import java.util.stream.Collectors

class ReportsFragment: Fragment() {

    private lateinit var binding: FragmentReportsBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_reports, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {
            reportPhoneNumber.setOnClickListener {
              findNavController().navigate(ReportsFragmentDirections.actionReportsFragmentToReportPhoneFragment())
            }
            reportAgeWise.setOnClickListener {
                findNavController().navigate(ReportsFragmentDirections.actionReportsFragmentToReportAgeFragment())
            }
            reportColor.setOnClickListener {
                findNavController().navigate(ReportsFragmentDirections.actionReportsFragmentToReportColorWiseFragment())
            }
            reportNotVoted.setOnClickListener {

            }
        }
    }
}