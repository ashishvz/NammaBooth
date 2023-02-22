package com.payoman.nammabooth.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.payoman.nammabooth.R
import com.payoman.nammabooth.adapters.ReportFragmentAdapter
import com.payoman.nammabooth.databinding.FragmentReportBinding
import com.payoman.nammabooth.models.Reports
import com.payoman.nammabooth.utils.Constants
import com.payoman.nammabooth.viewmodels.ReportViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import java.util.stream.Collectors

class ReportsFragment: Fragment() {

    private lateinit var binding: FragmentReportBinding
    private val viewModel: ReportViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_report, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {
            loadingProgressBar.show()
            noData.visibility = View.GONE
            runBlocking(Dispatchers.IO) {
                viewModel.getReports(Constants.PARTY_ID.toString(), Constants.ELECTION_ID.toString())
            }
        }
        setObservers()
    }

    private fun setObservers() {
        viewModel.reports.observe(viewLifecycleOwner) {
            if (it != null && it.status == "SUCCESS") {
                val reports = it.data
                val reportList: MutableList<MutableList<Reports>> = mutableListOf()
                val reportsMap = reports.stream().collect(Collectors.groupingBy(Reports::voterId))
                for (entry: Map.Entry<String, List<Reports>> in reportsMap.entries) {
                    reportList.add(entry.value.toMutableList())
                }
                if (reportList.size > 0) {
                    Log.d("List", reportList.toString())
                    binding.loadingProgressBar.hide()
                    binding.noData.visibility = View.GONE
                    binding.recyclerView.layoutManager = LinearLayoutManager(requireActivity())
                    binding.recyclerView.adapter = ReportFragmentAdapter(requireActivity(), reportList)
                } else {
                    binding.loadingProgressBar.hide()
                    binding.noData.visibility = View.VISIBLE
                }
            } else {
                binding.loadingProgressBar.hide()
                binding.noData.visibility = View.VISIBLE
            }
        }
    }

}