package com.payoman.nammabooth.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.payoman.nammabooth.R
import com.payoman.nammabooth.databinding.ReportCardBinding
import com.payoman.nammabooth.models.Reports

class ReportFragmentAdapter(
    private val context: Context,
    private val reportList: MutableList<MutableList<Reports>>
) : RecyclerView.Adapter<ReportFragmentAdapter.ViewHolder>() {

    private lateinit var binding: ReportCardBinding

    inner class ViewHolder : RecyclerView.ViewHolder(binding.root) {
        fun setData(report: MutableList<Reports>) {
            binding.apply {
                voterIdText.text = report[0].voterId
                reportRecycler.layoutManager = LinearLayoutManager(context)
                reportRecycler.adapter = ReportsAdapter(context, report)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        binding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.report_card, parent, false)
        return ViewHolder()
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
       holder.setData(reportList[position])
    }

    override fun getItemCount(): Int {
       return reportList.size
    }
}