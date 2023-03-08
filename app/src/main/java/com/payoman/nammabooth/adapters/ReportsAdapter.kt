package com.payoman.nammabooth.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.payoman.nammabooth.R
import com.payoman.nammabooth.databinding.ReportQnaLayoutBinding
import com.payoman.nammabooth.models.Reports

class ReportsAdapter(
    private val context: Context,
    private val reportList: MutableList<Reports>
) : RecyclerView.Adapter<ReportsAdapter.ViewHolder>() {

    private lateinit var binding: ReportQnaLayoutBinding

    inner class ViewHolder : RecyclerView.ViewHolder(binding.root) {
        fun setData(report: Reports) {
            binding.apply {
                questionText.text = report.question
                answerText.text = report.answerSelected
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        binding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.report_qna_layout, parent, false)
        return ViewHolder()
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.setData(reportList[position])
    }

    override fun getItemCount(): Int {
      return reportList.size
    }
}