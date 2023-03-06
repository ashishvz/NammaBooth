package com.payoman.nammabooth.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.payoman.nammabooth.R
import com.payoman.nammabooth.database.Voter
import com.payoman.nammabooth.databinding.CardReportPhoneBinding
import com.payoman.nammabooth.interfaces.OnVoterCardClickListener
import java.util.*

class ReportPhoneAdapter(private val onVoterCardClickListener: OnVoterCardClickListener): RecyclerView.Adapter<ReportPhoneAdapter.ViewHolder>() {

    private lateinit var binding: CardReportPhoneBinding
    private val voterList = mutableListOf<Voter>()

    inner class ViewHolder: RecyclerView.ViewHolder(binding.root) {
        fun bindData(voter: Voter) {
            binding.apply {
                voterSLNO.text = voter.sno
                voterId.text = voter.voterId ?: "NA"
                voterName.text = String.format(Locale.ENGLISH, "%s(%s)", voter.voterNameEn ?: "NA", voter.voterNameKan ?: "NA")
                voterPartNoName.text = String.format(Locale.ENGLISH, "%s", voter.partNo)
                voterMobileNumber.text = if (voter.mobileNo.isNullOrBlank()) "NA" else voter.mobileNo
                phoneImage.setOnClickListener {
                    onVoterCardClickListener.OnClick(voter)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        binding = DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.card_report_phone, parent, false)
        return ViewHolder()
    }

    override fun getItemCount(): Int {
        return voterList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindData(voterList[position])
    }

    fun submitData(newVoterList: MutableList<Voter>) {
        voterList.clear()
        voterList.addAll(newVoterList)
        notifyDataSetChanged()
    }
}